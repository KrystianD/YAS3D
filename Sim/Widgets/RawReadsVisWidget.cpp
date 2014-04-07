#include "RawReadsVisWidget.h"
#include "ui_RawReadsVisWidget.h"

#include <QPainter>
#include <QTimer>
#include <QQuaternion>

RawReadsVisWidget::RawReadsVisWidget (QWidget* parent)
	: QWidget (parent), ui (new Ui::RawReadsVisWidget)
{
	ui->setupUi (this);

	ui->wAccelTop->installEventFilter (this);
	ui->wAccelBack->installEventFilter (this);
	ui->wAccelRight->installEventFilter (this);
	ui->wGyroTop->installEventFilter (this);
	ui->wGyroBack->installEventFilter (this);
	ui->wGyroRight->installEventFilter (this);
	ui->wMagTop->installEventFilter (this);
	ui->wMagBack->installEventFilter (this);
	ui->wMagRight->installEventFilter (this);

	QTimer *t = new QTimer (this);
	connect (t, SIGNAL(timeout()), this, SLOT(repaintAll()));
	t->setInterval (10);
	t->start ();
}

RawReadsVisWidget::~RawReadsVisWidget ()
{
	delete ui;
}

bool RawReadsVisWidget::eventFilter (QObject* sender, QEvent* event)
{
	if (event->type () == QEvent::Paint)
	{
		if (sender == ui->wAccelTop) { drawAccelTop (); return true; }
		if (sender == ui->wAccelBack) { drawAccelBack (); return true; }
		if (sender == ui->wAccelRight) { drawAccelRight (); return true; }

		if (sender == ui->wGyroTop) { drawGyroTop (); return true; }
		if (sender == ui->wGyroBack) { drawGyroBack (); return true; }
		if (sender == ui->wGyroRight) { drawGyroRight (); return true; }

		if (sender == ui->wMagTop) { drawMagTop (); return true; }
		if (sender == ui->wMagBack) { drawMagBack (); return true; }
		if (sender == ui->wMagRight) { drawMagRight (); return true; }
	}
	return false;
}

const int accelDiv = 50;
const int magDiv = 50;

void RawReadsVisWidget::repaintAll ()
{
	ui->wAccelTop->repaint ();
	ui->wAccelBack->repaint ();
	ui->wAccelRight->repaint ();

	ui->wGyroTop->repaint ();
	ui->wGyroBack->repaint ();
	ui->wGyroRight->repaint ();

	ui->wMagTop->repaint ();
	ui->wMagBack->repaint ();
	ui->wMagRight->repaint ();

	ui->lbAccelX->setText (QString::number (m_accel.x ()));
	ui->lbAccelY->setText (QString::number (m_accel.y ()));
	ui->lbAccelZ->setText (QString::number (m_accel.z ()));

	ui->lbGyroX->setText (QString::number (m_gyro.x ()));
	ui->lbGyroY->setText (QString::number (m_gyro.y ()));
	ui->lbGyroZ->setText (QString::number (m_gyro.z ()));

	ui->lbMagX->setText (QString::number (m_mag.x ()));
	ui->lbMagY->setText (QString::number (m_mag.y ()));
	ui->lbMagZ->setText (QString::number (m_mag.z ()));
}
void RawReadsVisWidget::drawAccelTop ()
{
	QPainter p (ui->wAccelTop);

	p.fillRect (ui->wAccelTop->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wAccelTop->width () / 2, ui->wAccelTop->height () / 2,
				ui->wAccelTop->width () / 2 - m_accel.x () / accelDiv, ui->wAccelTop->height () / 2 - m_accel.y () / accelDiv);
}
void RawReadsVisWidget::drawAccelBack ()
{
	QPainter p (ui->wAccelBack);

	p.fillRect (ui->wAccelBack->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wAccelBack->width () / 2, ui->wAccelBack->height () / 2,
				ui->wAccelBack->width () / 2 - m_accel.x () / accelDiv, ui->wAccelBack->height () / 2 - m_accel.z () / accelDiv);
}
void RawReadsVisWidget::drawAccelRight ()
{
	QPainter p (ui->wAccelRight);

	p.fillRect (ui->wAccelRight->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wAccelRight->width () / 2, ui->wAccelRight->height () / 2,
				ui->wAccelRight->width () / 2 - m_accel.y () / accelDiv, ui->wAccelRight->height () / 2 - m_accel.z () / accelDiv);
}

void RawReadsVisWidget::drawGyroTop ()
{
	QPainter p (ui->wGyroTop);

	p.fillRect (ui->wGyroTop->rect (), QBrush (Qt::white));

	QVector3D pt = QQuaternion::fromAxisAndAngle (0, 0, 1, m_gyro.z () / 50).rotatedVector (QVector3D (0, 100, 0));

	p.drawLine (
				ui->wGyroTop->width () / 2, ui->wGyroTop->height () / 2,
				ui->wGyroTop->width () / 2 - pt.x (), ui->wGyroTop->height () / 2 - pt.y ());
}
void RawReadsVisWidget::drawGyroBack ()
{
	QPainter p (ui->wGyroBack);

	p.fillRect (ui->wGyroBack->rect (), QBrush (Qt::white));

	QVector3D pt = QQuaternion::fromAxisAndAngle (0, 0, 1, -m_gyro.x () / 50).rotatedVector (QVector3D (0, 100, 0));

	p.drawLine (
				ui->wGyroBack->width () / 2, ui->wGyroBack->height () / 2,
				ui->wGyroBack->width () / 2 - pt.x (), ui->wGyroBack->height () / 2 - pt.y ());
}
void RawReadsVisWidget::drawGyroRight ()
{
	QPainter p (ui->wGyroRight);

	p.fillRect (ui->wGyroRight->rect (), QBrush (Qt::white));

	QVector3D pt = QQuaternion::fromAxisAndAngle (0, 0, 1, m_gyro.y () / 50).rotatedVector (QVector3D (0, 100, 0));

	p.drawLine (
				ui->wGyroRight->width () / 2, ui->wGyroRight->height () / 2,
				ui->wGyroRight->width () / 2 - pt.x (), ui->wGyroRight->height () / 2 - pt.y ());
}

void RawReadsVisWidget::drawMagTop ()
{
	QPainter p (ui->wMagTop);

	p.fillRect (ui->wMagTop->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wMagTop->width () / 2, ui->wMagTop->height () / 2,
				ui->wMagTop->width () / 2 - m_mag.x () / magDiv, ui->wMagTop->height () / 2 - m_mag.y () / magDiv);
}
void RawReadsVisWidget::drawMagBack ()
{
	QPainter p (ui->wMagBack);

	p.fillRect (ui->wMagBack->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wMagBack->width () / 2, ui->wMagBack->height () / 2,
				ui->wMagBack->width () / 2 - m_mag.x () / magDiv, ui->wMagBack->height () / 2 - m_mag.z () / magDiv);
}
void RawReadsVisWidget::drawMagRight ()
{
	QPainter p (ui->wMagRight);

	p.fillRect (ui->wMagRight->rect (), QBrush (Qt::white));

	p.drawLine (
				ui->wMagRight->width () / 2, ui->wMagRight->height () / 2,
				ui->wMagRight->width () / 2 - m_mag.y () / magDiv, ui->wMagRight->height () / 2 - m_mag.z () / magDiv);
}
