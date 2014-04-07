#include "MainWindow.h"
#include <QApplication>

#include <QTextCodec>

int main(int argc, char *argv[])
{
	libusb_init (0);

	QTextCodec::setCodecForCStrings (QTextCodec::codecForName ("utf-8"));
	QTextCodec::setCodecForTr (QTextCodec::codecForName ("utf-8"));
	QTextCodec::setCodecForLocale (QTextCodec::codecForName ("utf-8"));

	QApplication a(argc, argv);
	MainWindow w;
	w.show();
	
	int r = a.exec();

	//libusb_exit (0);

	return r;
}