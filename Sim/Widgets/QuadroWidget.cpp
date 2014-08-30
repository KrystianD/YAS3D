#include "QuadroWidget.h"
#include "ui_QuadroWidget.h"

#include "utils.h"

QuadroWidget::QuadroWidget (QWidget* parent)
	: QWidget (parent), ui (new Ui::QuadroWidget)
{
	ui->setupUi (this);
}

QuadroWidget::~QuadroWidget ()
{
	delete ui;
}

void QuadroWidget::updateInfo (const TQuadroData& data)
{
	ui->quadroWidget->rotationQuat = data.quat;

	ui->lbCell1->setText (QString ("%1 V").arg (data.cell1, 0, 'f', 2));
	ui->lbCell2->setText (QString ("%1 V").arg (data.cell2, 0, 'f', 2));
	ui->lbCell3->setText (QString ("%1 V").arg (data.cell3, 0, 'f', 2));
	ui->lbVsupply->setText (QString ("%1 V").arg (data.Vsupply, 0, 'f', 2));
	ui->lbVcc->setText (QString ("%1 V").arg (data.Vcc, 0, 'f', 2));

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

	ui->lbPressure->setText (QString::number (data.pressure));
	ui->lbLandPressure->setText (QString::number (data.landPressure));
	ui->lbAltitude->setText (QString::number (data.altitude, 'f', 2));

	ui->lbFix->setText (QString::number (data.gpsFix));
	ui->lbSats->setText (QString::number (data.gpsSattelites));
	ui->lbLat->setText (QString::number (data.gpsLat, 'f', 5));
	ui->lbLon->setText (QString::number (data.gpsLon, 'f', 5));
	ui->lbGpsAlt->setText (QString::number (data.gpsAlt, 'f', 2));

}
