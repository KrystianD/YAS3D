#include "MainWindow.h"
#include "ui_MainWindow.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <stdio.h>
#include <cmath>

#include <QDebug>
#include <QUdpSocket>
#include <QDateTime>

#include "MadgwickAHRS.h"
#include "kutils.h"
// #include <QGLContext>

#include "utils.h"
#include "globals.h"

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
	
	ui->widget->installEventFilter(this);
	
	QTimer *tm = new QTimer(this);
	connect(tm, SIGNAL(timeout()), this, SLOT(draw()));
	tm->setInterval(1000 / 60);
	tm->start();
	
	m_udp.bind(9998);
	
	connect(&m_udp, SIGNAL(readyRead()), this, SLOT(processData()));
	
	// ui->visFull = new RemoteGL(&glContext, ui->centralWidget);
	// ui->visFull->setGeometry(QRect(250, 230, 231, 211));
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
		
		uint8_t *udpData = (uint8_t*)datagram.data();
		
		switch (udpData[0])
		{
		case TYPE_SENSORS:
			processSensorsData((TUdpDataSENSORS*)udpData);
			break;
		}
		cnt++;
	}
	qDebug() << "WQE" << cnt;
}
void MainWindow::processSensorsData(TUdpDataSENSORS* data)
{
	int32_t time = data->ticks;
	
	m_pdaData.ax = -data->ax;
	m_pdaData.ay = -data->ay;
	m_pdaData.az = -data->az;
	m_pdaData.gx = data->gx;
	m_pdaData.gy = data->gy;
	m_pdaData.gz = data->gz;
	m_pdaData.mx = -data->mx;
	m_pdaData.my = -data->my;
	m_pdaData.mz = -data->mz;
	
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
	last = time;
	// qDebug() << time << diff;
	
	MadgwickAHRSupdateIMU(m_accelQuat, 0, 0, 0, m_pdaData.ax, m_pdaData.ay, m_pdaData.az, (float)diff / 1000.0f);
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
	
	// updateInfo();
	// qDebug() << "A";
	
	ui->visAccel->updateGL();
	ui->visAccelMagnet->updateGL();
	ui->visGyroAccel->updateGL();
	ui->visGyro->updateGL();
	ui->visFull->updateGL();
}
void MainWindow::updateInfo()
{
}
