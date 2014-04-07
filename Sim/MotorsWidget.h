#ifndef MOTORSWIDGET_H
#define MOTORSWIDGET_H

#include <QWidget>

namespace Ui
{
class MotorsWidget;
}

class MotorsWidget : public QWidget
{
	Q_OBJECT
	
public:
	explicit MotorsWidget (QWidget *parent = 0);
	~MotorsWidget ();

	float px, py;
	float px2, py2;
	quint8 m1, m2, m3, m4;

private:
	Ui::MotorsWidget *ui;

	bool eventFilter (QObject *sender, QEvent *event);

	void draw ();
};

#endif // MOTORSWIDGET_H
