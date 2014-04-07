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

#include "Dialogs/BaroChart.h"

#include "kutils.h"

#include "utils.h"
#include "globals.h"

QUdpSocket m_udp;

MainWindow::MainWindow (QWidget *parent)
	: QMainWindow (parent), ui (new Ui::MainWindow)
{
	ui->setupUi (this);

	connect (&m_tm, SIGNAL(timeout()), this, SLOT(processData()));
	m_tm.setInterval (1);
	m_tm.start ();

	ui->widget->installEventFilter (this);

	QTimer *tm = new QTimer (this);
	connect (tm, SIGNAL(timeout()), this, SLOT(repaint()));
	tm->setInterval (100);
	tm->start ();

	m_udp.bind(9999);
}

MainWindow::~MainWindow ()
{
	delete ui;
}

#include "MadgwickAHRS.h"

QQuaternion FromEuler(float pitch, float yaw, float roll)
{
	// Basically we create 3 Quaternions, one for pitch, one for yaw, one for roll
	// and multiply those together.
	// the calculation below does the same, just shorter

	float p = pitch   / 2.0;
	float ya = yaw    / 2.0;
	float r = roll   / 2.0;

	float sinp = sin(p);
	float siny = sin(ya);
	float sinr = sin(r);
	float cosp = cos(p);
	float cosy = cos(ya);
	float cosr = cos(r);

	float x = sinr * cosp * cosy - cosr * sinp * siny;
	float y = cosr * sinp * cosy + sinr * cosp * siny;
	float z = cosr * cosp * siny - sinr * sinp * cosy;
	float w = cosr * cosp * cosy + sinr * sinp * siny;

	return QQuaternion (w, x, y, z);
}


float minX = 30000, minY = 30000, minZ = 30000;
float maxX = -30000, maxY = -30000, maxZ = -30000;

void MainWindow::processData ()
{
	while(m_udp.hasPendingDatagrams())
	{
		QByteArray datagram;
		datagram.resize(m_udp.pendingDatagramSize());
		QHostAddress sender;
		quint16 senderPort;
qDebug () << "DG" << datagram.size();
		m_udp.readDatagram(datagram.data(), datagram.size(), &sender, &senderPort);

		float *val = (float*)datagram.data();
		int32_t *val2 = (int32_t*)datagram.data();

		float ax = -val[0];
		float ay = -val[1];
		float az = -val[2];
		float gx = val[3];
		float gy = val[4];
		float gz = val[5];
		float mx = -val[6];
		float my = -val[7];
		float mz = -val[8];
		int64_t time = *(int64_t*)(datagram.data()+9*4);

		if (mx < minX) minX = mx;
		if (my < minY) minY = my;
		if (mz < minZ) minZ = mz;
		if (mx > maxX) maxX = mx;
		if (my > maxY) maxY = my;
		if (mz > maxZ) maxZ = mz;


		//-66.75 -46.75 -40.25
		// 40.75 40.5 40.5

		static int64_t last = 0;
		if (last==0)
			last=time;
		int64_t diff = time-last;
		last = time;
		qDebug()<<time << diff;

		//gx=gy=gz=0;
		MadgwickAHRSupdate(gx, gy, gz, ax, ay, az, mx, my, mz, (float)diff/1000.0f);

		ui->remoteWidget->rotationQuat = QQuaternion::fromAxisAndAngle(0,0,1,-90);
		ui->remoteWidget->rotationQuat *= QQuaternion(q0,q1,q2,q3);
		//qDebug () << q0 << q1 << q2 << q3;

		//qDebug() << QString("Received datagram of size: %1").arg(datagram.size());

		QString txt = "";

			txt += QString ("rq0: %1 rq1: %2 rq2: %3 rq3: %4\n")
					.arg (q0).arg (q1).arg (q2).arg (q3);

			txt += QString ("ax: %1 ay: %2 az: %3\n")
					.arg (ax,10,'f',2).arg (ay,10,'f',2).arg (az,10,'f',2);

			txt += QString ("gx: %1 gy: %2 gz: %3\n")
					.arg (gx,10,'f',2).arg (gy,10,'f',2).arg (gz,10,'f',2);

			txt += QString ("mx: %1 my: %2 mz: %3")
					.arg (mx,10,'f',2).arg (my,10,'f',2).arg (mz,10,'f',2);

			//txt += QString ("pitch: %1 roll: %2 yaw: %3\n")
			//		.arg (quadro.pitch).arg (quadro.roll).arg (quadro.yaw);

			ui->label->setText (txt);
	}
}

bool MainWindow::eventFilter (QObject *sender, QEvent *event)
{
	if (event->type () == QEvent::Paint)
	{
		draw ();
		return true;
	}
	return false;
}

void MainWindow::draw ()
{
	QPainter p (ui->widget);
/*
	const int MOTOR_RADIUS = 20 / 2 * 3;
	const int MOTOR_DISTANCE = 17.5 * 3;
*/
	p.fillRect (rect (), Qt::white);

	/*p.setPen (QPen (QColor (0, 0, 0, 255)));

	p.setBrush (QBrush (QColor (255, 0, 0, m1)));
	p.drawEllipse (QPoint (width () / 2 - MOTOR_DISTANCE, height () / 2), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m3)));
	p.drawEllipse (QPoint (width () / 2 + MOTOR_DISTANCE, height () / 2), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m2)));
	p.drawEllipse (QPoint (width () / 2, height () / 2 - MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m4)));
	p.drawEllipse (QPoint (width () / 2, height () / 2 + MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);

	p.setBrush (QBrush (QColor (255, 0, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2, height () / 2), 3, 3);

	p.setBrush (QBrush (QColor (255, 0, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2 + px * MOTOR_DISTANCE, height () / 2 - py * MOTOR_DISTANCE), 3, 3);
*/

	/*QVector3D quadroMagn (-quadro.my, quadro.mx, -quadro.mz);
	quadroMagn.normalize ();

	QVector3D quadroAccel (quadro.ax, quadro.ay, quadro.az);
	quadroAccel.normalize ();


	p.setPen (QPen (QColor (0, 0, 255, 255), 2));
	p.drawLine (50, 0, 50 + quadroMagn.y () * 100, quadroMagn.z () * 100);

	p.setPen (QPen (QColor (255, 0, 0, 255), 2));
	p.drawLine (50, 0, 50 + quadroAccel.y () * 100, quadroAccel.z () * 100);
*/
	updateInfo ();
}
void MainWindow::updateInfo ()
{
	/*QString txt = "";

	txt += QString ("qq0: %1 qq1: %2 qq2: %3 qq3: %4\n")
			.arg (quadro.q0).arg (quadro.q1).arg (quadro.q2).arg (quadro.q3);

	txt += QString ("ax: %1 ay: %2 az: %3\n")
			.arg (quadro.ax).arg (quadro.ay).arg (quadro.az);

	txt += QString ("pitch: %1 roll: %2 yaw: %3\n")
			.arg ((int)r2d (quadro.pitch)).arg ((int)r2d (quadro.roll)).arg ((int)r2d (quadro.yaw));

	txt += QString ("m1: %1 m2: %2 m3: %3 m4: %4")
			.arg (quadro.m1).arg (quadro.m2).arg (quadro.m3).arg (quadro.m4);

	ui->lbQuadro->setText (txt);
*/
	/*txt = "";

	txt += QString ("rq0: %1 rq1: %2 rq2: %3 rq3: %4\n")
			.arg (remote.rq0).arg (remote.rq1).arg (remote.rq2).arg (remote.rq3);

	txt += QString ("ax: %1 ay: %2 az: %3\n")
			.arg (remote.ax).arg (remote.ay).arg (remote.az);

	txt += QString ("mx: %1 my: %2 mz: %3")
			.arg (remote.mx).arg (remote.my).arg (remote.mz);

	//txt += QString ("pitch: %1 roll: %2 yaw: %3\n")
	//		.arg (quadro.pitch).arg (quadro.roll).arg (quadro.yaw);

	ui->lbRemote->setText (txt);*/
}
