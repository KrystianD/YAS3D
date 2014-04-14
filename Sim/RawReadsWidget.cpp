#include "RawReadsWidget.h"
#include "ui_RawReadsWidget.h"

#include "utils.h"

RawReadsWidget::RawReadsWidget(QWidget* parent)
	: QWidget(parent), ui(new Ui::RawReadsWidget)
{
	ui->setupUi(this);
	
	/*ui->widgetPitch->installEventFilter (this);
	ui->widgetRoll->installEventFilter (this);
	ui->widgetYaw->installEventFilter (this);*/
}

RawReadsWidget::~RawReadsWidget()
{
	delete ui;
}

void RawReadsWidget::updateInfo(const TData &data)
{
	ui->lbAccelX->setText(QString::number(data.ax, 'f', 2));
	ui->lbAccelY->setText(QString::number(data.ay, 'f', 2));
	ui->lbAccelZ->setText(QString::number(data.az, 'f', 2));
	
	ui->lbGyroX->setText(QString::number(r2d(data.gx), 'f', 2));
	ui->lbGyroY->setText(QString::number(r2d(data.gy), 'f', 2));
	ui->lbGyroZ->setText(QString::number(r2d(data.gz), 'f', 2));
	
	ui->lbMagX->setText(QString::number(data.mx, 'f', 2));
	ui->lbMagY->setText(QString::number(data.my, 'f', 2));
	ui->lbMagZ->setText(QString::number(data.mz, 'f', 2));
	
	ui->lbPitch->setText(QString::number(r2d(data.pitch), 'f', 1));
	ui->lbRoll->setText(QString::number(r2d(data.roll), 'f', 1));
	ui->lbYaw->setText(QString::number(r2d(data.yaw), 'f', 1));
}

bool RawReadsWidget::eventFilter(QObject* sender, QEvent* event)
{
	return false;
}
