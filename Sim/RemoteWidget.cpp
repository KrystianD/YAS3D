#include "RemoteWidget.h"
#include "ui_RemoteWidget.h"

#include "utils.h"

RemoteWidget::RemoteWidget (QWidget* parent)
	: QWidget (parent), ui (new Ui::RemoteWidget)
{
	ui->setupUi (this);

	/*ui->widgetPitch->installEventFilter (this);
	ui->widgetRoll->installEventFilter (this);
	ui->widgetYaw->installEventFilter (this);*/
}

RemoteWidget::~RemoteWidget ()
{
	delete ui;
}

void RemoteWidget::updateInfo (const TData &data)
{
	ui->lbAccelX->setText (QString::number (data.ax));
	ui->lbAccelY->setText (QString::number (data.ay));
	ui->lbAccelZ->setText (QString::number (data.az));

	ui->lbGyroX->setText (QString::number (data.gx));
	ui->lbGyroY->setText (QString::number (data.gy));
	ui->lbGyroZ->setText (QString::number (data.gz));

	ui->lbMagX->setText (QString::number (data.mx));
	ui->lbMagY->setText (QString::number (data.my));
	ui->lbMagZ->setText (QString::number (data.mz));

	ui->lbPitch->setText (QString::number (r2d (data.pitch), 'f', 1) + "°");
	ui->lbRoll->setText (QString::number (r2d (data.roll), 'f', 1) + "°");
	ui->lbYaw->setText (QString::number (r2d (data.yaw), 'f', 1) + "°");
}

bool RemoteWidget::eventFilter (QObject* sender, QEvent* event)
{
	return false;
}
