#ifndef QUADROWIDGET_H
#define QUADROWIDGET_H

#include <QWidget>

#include "classes.h"

namespace Ui
{
	class QuadroWidget;
}

class QuadroWidget : public QWidget
{
	Q_OBJECT

public:
	explicit QuadroWidget (QWidget* parent = 0);
	~QuadroWidget ();

	void updateInfo (const TQuadroData& data);

private:
	Ui::QuadroWidget *ui;
};

#endif // QUADROWIDGET_H
