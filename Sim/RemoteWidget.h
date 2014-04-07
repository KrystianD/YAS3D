#ifndef REMOTEWIDGET_H
#define REMOTEWIDGET_H

#include <QWidget>

#include "classes.h"

namespace Ui
{
	class RemoteWidget;
}

class RemoteWidget : public QWidget
{
	Q_OBJECT

public:
	explicit RemoteWidget (QWidget* parent = 0);
	~RemoteWidget ();

	void updateInfo (const TData& data);

private:
	Ui::RemoteWidget *ui;

	bool eventFilter (QObject* sender, QEvent* event);
};

#endif // REMOTEWIDGET_H
