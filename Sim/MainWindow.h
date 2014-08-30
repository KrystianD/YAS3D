#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTimer>
#include <QQuaternion>
#include <QFile>

#include "globals.h"
#include "classes.h"

namespace Ui
{
class MainWindow;
}

class MainWindow : public QMainWindow
{
	Q_OBJECT
	
public:
	explicit MainWindow(QWidget *parent = 0);
	~MainWindow();
	
private slots:
	void processData();
	void draw();
	
	void on_pbAccel_clicked();

	void on_pbGyro_clicked();

	void on_pbGyroAccel_clicked();

	void on_pbAccelMagnet_clicked();

	void on_pbFull_clicked();

private:
	Ui::MainWindow *ui;
	
	QTimer m_tm;
	TData m_pdaData;

	QQuaternion m_accelQuat, m_gyroQuat, m_gyroAccelQuat, m_accelMagnetQuat, m_fullQuat;
	
	bool eventFilter(QObject *sender, QEvent *event);
	
	void updateInfo();
	
	void processSensorsData(TUdpDataSENSORS* data);
};

#endif // MAINWINDOW_H
