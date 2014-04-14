#ifndef CLASSES_H
#define CLASSES_H

#include <stdint.h>

#include <QQuaternion>
#include <QVector>
#include <QVector3D>

#include "utils.h"

class Enemy
{
public:
	QVector<QVector3D> points;
};

struct TData
{
	QQuaternion worldQuat;
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float pitch, roll, yaw;
	
	QVector<QVector3D> playerPoints, foodPoints;
	QVector<Enemy> enemies;
	
	uint8_t sphereEnabled;

	QVector3D frustum[8];
};

#define TYPE_SENSORS 0
#define TYPE_PLAYER  1
#define TYPE_STAB    2
#define TYPE_ENEMY   3
#define TYPE_FOOD    4

#define NO_SPHERE 0
#define SPHERE    1

#pragma pack(1)
struct TUdpDataSENSORS
{
	uint8_t type;
	float frustumPoints[8][3];
	float ax, ay, az, gx, gy, gz, mx, my, mz;
	float q0, q1, q2, q3;
	uint64_t ticks;
	uint8_t sphereEnabled, isStabilized;
	// float 
};
struct TUdpDataPLAYER
{
	uint8_t type;
	uint16_t playerSize;
};
struct TUdpDataENEMY
{
	uint8_t type;
	uint16_t idx;
	uint16_t enemiesSize;
};
struct TUdpDataFOOD
{
	uint8_t type;
	uint16_t foodSize;
};

#pragma pack()

#endif // CLASSES_H
