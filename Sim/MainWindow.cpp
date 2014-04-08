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
#include <QLayout>

#include "MadgwickAHRS.h"
#include "kutils.h"
#include <QOpenGLContext>

#include "utils.h"
#include "globals.h"
#include "RemoteGL.h"

QUdpSocket m_udp;
float minX = 30000, minY = 30000, minZ = 30000;
float maxX = -30000, maxY = -30000, maxZ = -30000;
QOpenGLContext *glContext;

RemoteGL *visAccel, *visGyro, *visGyroAccel, *visAccelMagnet, *visFull;


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
	
	glContext= new QOpenGLContext();
	QSurfaceFormat f;
	f.setSwapBehavior(QSurfaceFormat::DoubleBuffer);
	glContext->setFormat(f);
qDebug ()<<"ctx"<<	glContext->create();


	visAccel = new RemoteGL(glContext);
	visAccelMagnet = new RemoteGL(glContext);
	visFull = new RemoteGL(glContext);
	visGyro = new RemoteGL(glContext);
	visGyroAccel = new RemoteGL(glContext);

	ui->visAccel->setLayout(new QHBoxLayout());
	ui->visAccelMagnet->setLayout(new QHBoxLayout());
	ui->visFull->setLayout(new QHBoxLayout());
	ui->visGyro->setLayout(new QHBoxLayout());
	ui->visGyroAccel->setLayout(new QHBoxLayout());

	ui->visAccel->layout()->addWidget(QWidget::createWindowContainer(visAccel));
	ui->visAccelMagnet->layout()->addWidget(QWidget::createWindowContainer(visAccelMagnet));
	ui->visFull->layout()->addWidget(QWidget::createWindowContainer(visFull));
	ui->visGyro->layout()->addWidget(QWidget::createWindowContainer(visGyro));
	ui->visGyroAccel->layout()->addWidget(QWidget::createWindowContainer(visGyroAccel));


	//visAccel->m_ctx->makeCurrent(visAccel);
	//visAccel->initializeGL();
//rgl->initializeOpenGLFunctions();

	/*ui->visAccel->setContext(glContext);
	ui->visAccelMagnet->setContext(glContext);
	ui->visFull->setContext(glContext);
	ui->visGyro->setContext(glContext);
	ui->visGyroAccel->setContext(glContext);*/
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
	
	visAccel->rotationQuat = m_accelQuat;
	visGyro->rotationQuat = m_gyroQuat;
	visGyroAccel->rotationQuat = m_gyroAccelQuat;
	
	visAccelMagnet->rotationQuat = QQuaternion::fromAxisAndAngle(0, 0, 1, -90);
	visAccelMagnet->rotationQuat *= m_accelMagnetQuat;
	visFull->rotationQuat = QQuaternion::fromAxisAndAngle(0, 0, 1, -90);
	visFull->rotationQuat *= m_fullQuat;
	
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

	//qDebug () << "go";
	QCoreApplication::postEvent(visAccel, new QEvent(QEvent::UpdateRequest));
	QCoreApplication::postEvent(visGyro, new QEvent(QEvent::UpdateRequest));
	QCoreApplication::postEvent(visGyroAccel, new QEvent(QEvent::UpdateRequest));
	QCoreApplication::postEvent(visAccelMagnet, new QEvent(QEvent::UpdateRequest));
	QCoreApplication::postEvent(visFull, new QEvent(QEvent::UpdateRequest));
	//rgl->paintGL();
	/*ui->visAccel->updateGL();
	ui->visAccelMagnet->updateGL();
	ui->visGyroAccel->updateGL();
	ui->visGyro->updateGL();
	ui->visFull->updateGL();*/
}
void MainWindow::updateInfo()
{
}
