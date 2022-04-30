package com.jay.base.point;

import org.springframework.stereotype.Service;

@Service
public class PointCalculateService implements PointService{

    private static final int POINT_RATE = 1;

    @Override
    public int getPoint(final int price) {
        return price * POINT_RATE / 100;
    }

}
