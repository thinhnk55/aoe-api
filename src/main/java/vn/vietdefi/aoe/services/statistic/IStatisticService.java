package vn.vietdefi.aoe.services.statistic;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.statistic.logic.Statistic;

public interface IStatisticService {
    JsonObject updateStatistic();
    JsonObject adminCallBackUpdateStatistic();
    JsonObject getStatistic();

    JsonObject getAllStatistic();


}
