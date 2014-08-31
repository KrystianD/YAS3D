#ifndef REFERENCEWIDGET_H
#define REFERENCEWIDGET_H

#include <QWidget>

namespace Ui
{
	class ReferenceWidget;
}

class ReferenceWidget : public QWidget
{
	Q_OBJECT
	
public:
	explicit ReferenceWidget (QWidget *parent = 0);
	~ReferenceWidget ();

	quint16 ax, ay, az, mx, my, mz;

private:
	Ui::ReferenceWidget *ui;

	bool eventFilter (QObject *sender, QEvent *event);

	void draw ();
};

#endif // REFERENCEWIDGET_H
