#ifndef BAROCHART_H
#define BAROCHART_H

#include <QDialog>
#include <QColor>

#include "utils.h"
#include "classes.h"

namespace Ui
{
	class BaroChart;
}

class BaroChart : public QDialog
{
	Q_OBJECT

public:
	explicit BaroChart (QWidget* parent = 0);
	~BaroChart ();

private:
	Ui::BaroChart *ui;

	bool eventFilter (QObject *sender, QEvent *event);
	void draw ();

	void drawWaveform (QPainter& painter, const QColor& color);
};

#endif // BAROCHART_H
