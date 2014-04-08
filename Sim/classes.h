#ifndef CLASSES_H
#define CLASSES_H

#include <stdint.h>

#include <QQuaternion>
#include <QVector>
#include <QVector3D>

#include "utils.h"

struct TData
{
	QQuaternion worldQuat;
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float pitch, roll, yaw;

	QVector<QVector3D> playerPoints, foodPoints;

	uint8_t sphereEnabled;
};

#define TYPE_SENSORS 0
#define TYPE_OBJ     1
#define TYPE_STAB    2

#define NO_SPHERE 0
#define SPHERE    1

#pragma pack(1)
struct TUdpDataSENSORS
{
	uint8_t type;
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float q0, q1, q2, q3;
	uint64_t ticks;
	uint8_t sphereEnabled, isStabilized;
};
struct TUdpDataOBJ
{
	uint8_t type;
	uint16_t playerSize, foodSize;
};
#pragma pack()

#endif // CLASSES_H
