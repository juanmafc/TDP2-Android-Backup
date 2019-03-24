package fiubatdp2g1_hoycomo.hoycomo.model;

import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class LeadTimeStatistics {
    public long accummulatedLeadTime = 0;
    public int completedOrders = 0;

    public String getAverageLeadTimeString() {
        double averageLeadTime = accummulatedLeadTime / completedOrders;
        return Utilities.doubleToStringWithTwoDigits( averageLeadTime );
    }
}