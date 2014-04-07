#include "ReferenceWidget.h"

#include <QPainter>
#include <QTimer>
#include <QBrush>
#include <QColor>
#include <QPen>
#include <QEvent>
#include <QVector3D>

ReferenceWidget::ReferenceWidget (QWidget *parent)
	: QWidget (parent)//, ui (new Ui::ReferenceWidget)
{
	//ui->setupUi (this);

	this->installEventFilter (this);

	/*QTimer *tm = new QTimer (this);
	connect (tm, SIGNAL(timeout()), this, SLOT(repaint()));
	tm->setInterval (100);
	tm->start ();*/

	//px = py = 0;
	//m1 = m2 = m3 = m4 = 0;
}

ReferenceWidget::~ReferenceWidget ()
{
	//delete ui;
}

bool ReferenceWidget::eventFilter (QObject *sender, QEvent *event)
{
	if (event->type () == QEvent::Paint)
	{
		draw ();
	}
}

void ReferenceWidget::draw ()
{
	QPainter p (this);

	const int MOTOR_RADIUS = 20 / 2 * 3;
	const int MOTOR_DISTANCE = 17.5 * 3;

	p.fillRect (rect (), Qt::white);

	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawLine (20, 20, 180, 20);

	//p.setPen (QPen (QColor (0, 0, 0, 255)));
	//p.drawEllipse (QPoint (width () / 2 + px * MOTOR_DISTANCE, height () / 2 - py * MOTOR_DISTANCE), 3, 3);

	QVector3D quadroMagn (-my, mx, -mz);
	quadroMagn.normalize ();

	QVector3D quadroAccel (ax, ay, az);
	quadroAccel.normalize ();

	p.setPen (QPen (QColor (0, 0, 255, 255), 2));
	p.drawLine (100, 20, 50 + quadroMagn.y (), 20 + quadroMagn.z ());

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

	p.setBrush (QBrush (QColor (0, 255, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2 + px2 * MOTOR_DISTANCE, height () / 2 - py2 * MOTOR_DISTANCE), 3, 3);*/
}
