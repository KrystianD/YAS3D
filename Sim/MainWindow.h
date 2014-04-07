#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTimer>
#include <QQuaternion>
#include <QFile>

#include <libusb-1.0/libusb.h>
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
	explicit MainWindow (QWidget *parent = 0);
	~MainWindow ();

private slots:
	void processData ();

private:
	Ui::MainWindow *ui;

	QTimer m_tm;

	bool eventFilter (QObject *sender, QEvent *event);

	void draw ();
	void updateInfo ();
};

#endif // MAINWINDOW_H
