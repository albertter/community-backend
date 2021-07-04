package com.bjut.community.service;

import java.util.Date;


public interface DataService {
    public void recordUV(String ip);

    /**
     * @param start start date
     * @param end   end date
     * @return
     */
    public long calculateUV(Date start, Date end);

    public void recordDAU(int userId);

    public long calculateDAU(Date start, Date end);
}
