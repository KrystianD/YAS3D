#ifndef RawReadsWidget_H
#define RawReadsWidget_H

#include <QWidget>

#include "classes.h"

namespace Ui
{
	class RawReadsWidget;
}

class RawReadsWidget : public QWidget
{
	Q_OBJECT

public:
	explicit RawReadsWidget (QWidget* parent = 0);
	~RawReadsWidget ();

	void updateInfo (const TData& data);

private:
	Ui::RawReadsWidget *ui;

	bool eventFilter (QObject* sender, QEvent* event);
};

#endif // REMOTEWIDGET_H
