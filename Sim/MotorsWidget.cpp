#include "MotorsWidget.h"

#include <QPainter>
#include <QTimer>
#include <QBrush>
#include <QColor>
#include <QPen>
#include <QEvent>

MotorsWidget::MotorsWidget (QWidget *parent)
	: QWidget (parent)//, ui (new Ui::MotorsWidget)
{
	//ui->setupUi (this);

	this->installEventFilter (this);

	QTimer *tm = new QTimer (this);
	connect (tm, SIGNAL(timeout()), this, SLOT(repaint()));
	tm->setInterval (100);
	tm->start ();

	px = py = 0;
	m1 = m2 = m3 = m4 = 0;
}

MotorsWidget::~MotorsWidget ()
{
	//delete ui;
}

bool MotorsWidget::eventFilter (QObject *sender, QEvent *event)
{
	if (event->type () == QEvent::Paint)
	{
		draw ();
	}
}

void MotorsWidget::draw ()
{
	QPainter p (this);

	const int MOTOR_RADIUS = 20 / 2 * 3;
	const int MOTOR_DISTANCE = 17.5 * 2;

	p.fillRect (rect (), Qt::white);

	p.setPen (QPen (QColor (0, 0, 0, 255)));

	p.setBrush (QBrush (QColor (255, 0, 0, m1)));
	p.drawEllipse (QPoint (width () / 2 - MOTOR_DISTANCE, height () / 2 - MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m2)));
	p.drawEllipse (QPoint (width () / 2 + MOTOR_DISTANCE, height () / 2 - MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m4)));
	p.drawEllipse (QPoint (width () / 2 - MOTOR_DISTANCE, height () / 2 + MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);
	p.setBrush (QBrush (QColor (255, 0, 0, m3)));
	p.drawEllipse (QPoint (width () / 2 + MOTOR_DISTANCE, height () / 2 + MOTOR_DISTANCE), MOTOR_RADIUS, MOTOR_RADIUS);

	p.setBrush (QBrush (QColor (255, 0, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2, height () / 2), 3, 3);

	p.setBrush (QBrush (QColor (255, 0, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2 + px * MOTOR_DISTANCE, height () / 2 - py * MOTOR_DISTANCE), 3, 3);

	p.setBrush (QBrush (QColor (0, 255, 0, 255)));
	p.setPen (QPen (QColor (0, 0, 0, 255)));
	p.drawEllipse (QPoint (width () / 2 + px2 * MOTOR_DISTANCE, height () / 2 - py2 * MOTOR_DISTANCE), 3, 3);
}
