#include "MainWindow.h"
#include "ui_MainWindow.h"
#include <stdio.h>
#include <stdio.h>
#include <cmath>

#include <QDebug>
#include <QUdpSocket>
#include <QDateTime>

#include "MadgwickAHRS.h"
#include "kutils.h"
// #include <QGLContext>

#include "utils.h"
#include "classes.h"
#include "globals.h"
#include <QDataStream>

#include "objloader.hpp"

QUdpSocket m_udp;
float minX = 30000, minY = 30000, minZ = 30000;
float maxX = -30000, maxY = -30000, maxZ = -30000;
// QOpenGLContext glContext;


MainWindow::MainWindow(QWidget *parent)
	: QMainWindow(parent), ui(new Ui::MainWindow)
{
	ui->setupUi(this);

	/*connect(&m_tm, SIGNAL(timeout()), this, SLOT(processData()));
	m_tm.setInterval(1);
	m_tm.start();*/

	QTimer *tm = new QTimer(this);
	connect(tm, SIGNAL(timeout()), this, SLOT(draw()));
	tm->setInterval(1000 / 60);
	tm->start();

	m_udp.bind(9999);

	connect(&m_udp, SIGNAL(readyRead()), this, SLOT(processData()));

	ui->visWorld->stat = 0;
	ui->visWorld->pdaData = &m_pdaData;

	ui->visWorld2->stat = 1;
	ui->visWorld2->pdaData = &m_pdaData;

	ui->visScreen->pdaData = &m_pdaData;

	m_pdaData.sphereEnabled = 0;
	ui->widgetSensors->setVisible(true);
	ui->widgetSpheres->setVisible(false);
}

MainWindow::~MainWindow()
{
	delete ui;
}

void MainWindow::processData()
{
	int cnt = 0;
	while (m_udp.hasPendingDatagrams())
	{
		QByteArray datagram;
		datagram.resize(m_udp.pendingDatagramSize());
		QHostAddress sender;
		quint16 senderPort;

		m_udp.readDatagram(datagram.data(), datagram.size(), &sender, &senderPort);

		quint32 size = *(quint32*)datagram.data();
		QByteArray data = qUncompress(datagram);

		char *udpData = (char*)data.data();

		switch (udpData[0])
		{
		case TYPE_SENSORS:
			processSensorsData((TUdpDataSENSORS*)udpData);
			break;
		case TYPE_PLAYER:
		{
			TUdpDataPLAYER *d = (TUdpDataPLAYER*)udpData;

			for (int i = 0; i < 8; i++)
			{
				float x = d->frustumPoints[i][0];
				float y = d->frustumPoints[i][1];
				float z = d->frustumPoints[i][2];
				m_pdaData.frustum[i] = QVector3D(x, y, z);
			}

			QByteArray b(udpData + sizeof(TUdpDataPLAYER), data.size());
			QDataStream ds(b);
			ds.setByteOrder(QDataStream::LittleEndian);
			ds.setFloatingPointPrecision(QDataStream::SinglePrecision);

			m_pdaData.playerPoints.clear();

			for (int i = 0; i < d->playerSize; i++)
			{
				int16_t latU, lonU;
				ds >> latU >> lonU;
				m_pdaData.playerPoints.append(latlonUtoVector(latU, lonU));
			}

			ui->lbInfo->setText(QString("Points: %1\nLives: %2").arg(d->points).arg(d->lives));
			ui->lbInfo_2->setText(QString("Time left: %1:%2").arg(d->timeLeft / 60).arg((int)(d->timeLeft % 60), 2, 10, QChar('0')));

			break;
		}
		case TYPE_STAB:
			m_pdaData.sphereEnabled = udpData[1];
			break;
		case TYPE_ENEMY:
		{
			TUdpDataENEMY *d = (TUdpDataENEMY*)udpData;
			QByteArray b(udpData + sizeof(TUdpDataENEMY), data.size());
			QDataStream ds(b);
			ds.setByteOrder(QDataStream::LittleEndian);
			ds.setFloatingPointPrecision(QDataStream::SinglePrecision);

			while (m_pdaData.enemies.size() < d->idx + 1)
			{
				m_pdaData.enemies.append(Enemy());
			}

			m_pdaData.enemies[d->idx].points.clear();
			for (int i = 0; i < d->enemiesSize; i++)
			{
				int16_t latU, lonU;
				ds >> latU >> lonU;
				m_pdaData.enemies[d->idx].points.push_back(latlonUtoVector(latU, lonU));
			}
			break;
		}
		case TYPE_FOOD:
		{
			TUdpDataFOOD *d = (TUdpDataFOOD*)udpData;
			QByteArray b(udpData + sizeof(TUdpDataFOOD), data.size());
			QDataStream ds(b);
			ds.setByteOrder(QDataStream::LittleEndian);
			ds.setFloatingPointPrecision(QDataStream::SinglePrecision);

			m_pdaData.foodPoints.clear();

			for (int i = 0; i < d->foodSize; i++)
			{
				int16_t latU, lonU;
				ds >> latU >> lonU;
				m_pdaData.foodPoints.append(latlonUtoVector(latU, lonU));
			}

			break;
		}
		}
		cnt++;
	}
}
void MainWindow::processSensorsData(TUdpDataSENSORS* data)
{
	int32_t time = data->ticks;

	/*m_pdaData.frustum[0]*=10;
	m_pdaData.frustum[1]*=10;
	m_pdaData.frustum[2]*=10;
	m_pdaData.frustum[3]*=10;*/

	m_pdaData.sphereEnabled = data->sphereEnabled;
	m_pdaData.ax = -data->ax;
	m_pdaData.ay = -data->ay;
	m_pdaData.az = -data->az;
	m_pdaData.gx = data->gx;
	m_pdaData.gy = data->gy;
	m_pdaData.gz = data->gz;
	m_pdaData.mx = -data->mx;
	m_pdaData.my = -data->my;
	m_pdaData.mz = -data->mz;

	m_pdaData.worldQuat = QQuaternion(data->q0, data->q1, data->q2, data->q3);

	if (m_pdaData.mx < minX) minX = m_pdaData.mx;
	if (m_pdaData.my < minY) minY = m_pdaData.my;
	if (m_pdaData.mz < minZ) minZ = m_pdaData.mz;
	if (m_pdaData.mx > maxX) maxX = m_pdaData.mx;
	if (m_pdaData.my > maxY) maxY = m_pdaData.my;
	if (m_pdaData.mz > maxZ) maxZ = m_pdaData.mz;

	//-66.75 -46.75 -40.25
	// 40.75 40.5 40.5

	static int64_t last = 0;

	if (last == 0)
		last = time;
	int64_t diff = time - last;
	if (diff > 1000)
	{
		//qDebug() << "RES";

		m_accelQuat = QQuaternion(1, 0, 0, 0);
		m_gyroQuat = QQuaternion(1, 0, 0, 0);
		m_gyroAccelQuat = QQuaternion(1, 0, 0, 0);
		m_accelMagnetQuat = QQuaternion(1, 0, 0, 0);
		m_fullQuat = QQuaternion(1, 0, 0, 0);
		last = 0;
		diff = 100;
		return;
	}
	last = time;

	//qDebug() << "DIFF" << time << diff;

	MadgwickAHRSupdateIMU(m_accelQuat, 0, 0, 0, m_pdaData.ax, m_pdaData.ay, m_pdaData.az, (float)diff / 1000.0f * 5);
	MadgwickAHRSupdateIMU(m_gyroQuat, m_pdaData.gx, m_pdaData.gy, m_pdaData.gz, 0, 0, 0, (float)diff / 1000.0f);
	MadgwickAHRSupdateIMU(m_gyroAccelQuat, m_pdaData.gx, m_pdaData.gy, m_pdaData.gz, m_pdaData.ax, m_pdaData.ay, m_pdaData.az, (float)diff / 1000.0f);

	MadgwickAHRSupdate(m_accelMagnetQuat, 0, 0, 0, m_pdaData.ax, m_pdaData.ay, m_pdaData.az, m_pdaData.mx, m_pdaData.my, m_pdaData.mz, (float)diff / 1000.0f);
	MadgwickAHRSupdate(m_fullQuat, m_pdaData.gx, m_pdaData.gy, m_pdaData.gz, m_pdaData.ax, m_pdaData.ay, m_pdaData.az, m_pdaData.mx, m_pdaData.my, m_pdaData.mz, (float)diff / 1000.0f);

	ui->visAccel->rotationQuat = m_accelQuat;
	ui->visGyro->rotationQuat = m_gyroQuat;
	ui->visGyroAccel->rotationQuat = m_gyroAccelQuat;

	ui->visAccelMagnet->rotationQuat = QQuaternion::fromAxisAndAngle(0, 0, 1, -90);
	ui->visAccelMagnet->rotationQuat *= m_accelMagnetQuat;
	ui->visFull->rotationQuat = QQuaternion::fromAxisAndAngle(0, 0, 1, -90);
	ui->visFull->rotationQuat *= m_fullQuat;

	ui->visWorld->rotationQuat = m_pdaData.worldQuat.conjugate();
	ui->visWorld2->rotationQuat = m_pdaData.worldQuat.conjugate();
	ui->visScreen->rotationQuat = m_pdaData.worldQuat.conjugate();
	//static float q = 0;
	//ui->visWorld->rotationQuat *= QQuaternion::fromAxisAndAngle(0, 1, 1, q += 1);

	float qq0 = m_fullQuat.scalar();
	float qq1 = m_fullQuat.x();
	float qq2 = m_fullQuat.y();
	float qq3 = m_fullQuat.z();

	float q2_2 = qq2*qq2;
	m_pdaData.pitch = atanf (2.0f*(qq0*qq1 + qq2*qq3) / (1.0f - 2.0f*(qq1*qq1 + q2_2)));
	m_pdaData.roll = asinf (2.0f*(qq0*qq2 - qq3*qq1));
	m_pdaData.yaw = atan2f (2.0f*(qq0*qq3 + qq1*qq2), (1.0f - 2.0f*(q2_2 + qq3*qq3)));

	ui->rawReads->updateInfo(m_pdaData);
}

bool MainWindow::eventFilter(QObject *sender, QEvent *event)
{
	if (event->type() == QEvent::Paint)
	{
		draw();
		return true;
	}
	return false;
}

void MainWindow::draw()
{
	// QPainter p(ui->widget);
	// p.fillRect(rect(), Qt::white);

	if (!m_pdaData.sphereEnabled)
	{
		ui->widgetSensors->setVisible(true);
		ui->widgetSpheres->setVisible(false);
		ui->visAccel->updateGL();
		ui->visAccelMagnet->updateGL();
		ui->visGyroAccel->updateGL();
		ui->visGyro->updateGL();
		ui->visFull->updateGL();
	}
	else
	{
		ui->widgetSensors->setVisible(false);
		ui->widgetSpheres->setVisible(true);
		ui->visWorld->updateGL();
		ui->visWorld2->updateGL();
		ui->visScreen->updateGL();
	}
}
void MainWindow::updateInfo()
{
}
void MainWindow::on_pbAccel_clicked()
{
	m_accelQuat = QQuaternion(1, 0, 0, 0);
}
void MainWindow::on_pbGyro_clicked()
{
	m_gyroQuat = QQuaternion(1, 0, 0, 0);
}
void MainWindow::on_pbGyroAccel_clicked()
{
	m_gyroAccelQuat = QQuaternion(1, 0, 0, 0);
}
void MainWindow::on_pbAccelMagnet_clicked()
{
	m_accelMagnetQuat = QQuaternion(1, 0, 0, 0);
}
void MainWindow::on_pbFull_clicked()
{
	m_fullQuat = QQuaternion(1, 0, 0, 0);
}
