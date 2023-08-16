package vn.vietdefi.bank.logic;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.services.BankService;
import vn.vietdefi.bank.services.IBankService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.thread.ThreadPoolWorker;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BankController {
    private static BankController ins = null;
    public static BankController instance() {
        if (ins == null) {
            ins = new BankController();
        }
        return ins;
    }
    public IBankService bankService;
    private BankController(){
        bankService = new BankService();
        updateActiveBank();
    }

    /**
     *
     */
    private void updateActiveBank() {
        if(bankWorkerList == null){
            bankWorkerList = new LinkedList<>();
        }else{
            bankWorkerList.clear();
        }
        JsonObject response = bankService.getActiveBanks();
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonArray array = response.getAsJsonArray("data");
            for(int i = 0; i < array.size(); i++){
                JsonObject json = array.get(i).getAsJsonObject();
                BankAccount account = new BankAccount(json);
                BankWorker worker = new BankWorker(account);
                bankWorkerList.add(worker);
            }
        }
    }

    List<BankWorker> bankWorkerList;
    private void loop() {
        try {
            updateActiveBank();
            DebugLogger.info("BankController loop");
            for (BankWorker data : bankWorkerList) {
                data.loop();
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
        }
    }
    public ScheduledFuture<?> loopTask;
    public void startLoop() {
        DebugLogger.info("BankController startLoop");
        this.loopTask = ThreadPoolWorker.instance().scheduler.scheduleAtFixedRate(
                () -> {
                    this.loop();
                }, 0, 5, TimeUnit.SECONDS
        );
    }
    public void stopLoop() {
        if (loopTask != null) {
            this.loopTask.cancel(true);
        }
    }
}
