#ifndef RAWREADSVISWIDGET_H
#define RAWREADSVISWIDGET_H

#include <QWidget>
#include <QVector3D>

namespace Ui
{
	class RawReadsVisWidget;
}

class RawReadsVisWidget : public QWidget
{
	Q_OBJECT

public:
	explicit RawReadsVisWidget (QWidget* parent = 0);
	~RawReadsVisWidget ();

	void setData (QVector3D accel, QVector3D gyro, QVector3D mag) { m_accel = accel; m_gyro = gyro; m_mag = mag; }

private:
	Ui::RawReadsVisWidget *ui;

	QVector3D m_accel, m_gyro, m_mag;

	bool eventFilter (QObject* sender, QEvent* event);

	void drawAccelTop ();
	void drawAccelBack ();
	void drawAccelRight ();

	void drawGyroTop ();
	void drawGyroBack ();
	void drawGyroRight ();

	void drawMagTop ();
	void drawMagBack ();
	void drawMagRight ();

private slots:
	void repaintAll ();
};

#endif // RAWREADSVISWIDGET_H
