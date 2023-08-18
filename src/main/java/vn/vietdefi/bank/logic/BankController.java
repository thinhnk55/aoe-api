package vn.vietdefi.bank.logic;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.bank.AoeBankMessage;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.thread.ThreadPoolWorker;

import java.util.HashMap;
import java.util.Map;
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
    private BankController(){
        updateWorkingBank();
    }

    public void updateWorkingBank() {
        if(bankWorkers == null){
            bankWorkers = new HashMap<>();
        }else{
            bankWorkers.clear();
        }
        JsonObject response = BankServices.bankService.getWorkingBanks();
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonArray array = response.getAsJsonArray("data");
            for(int i = 0; i < array.size(); i++){
                JsonObject json = array.get(i).getAsJsonObject();
                BankAccount account = new BankAccount(json);
                BankWorker worker = new BankWorker(account);
                bankWorkers.put(worker.account.id, worker);
            }
        }
    }
    Map<Long, BankWorker> bankWorkers;
    private void loop() {
        try {
            DebugLogger.info("BankController loop {} ", bankWorkers.size());
            for (Map.Entry<Long, BankWorker> entry : bankWorkers.entrySet()) {
                //update acc
                BankWorker worker = entry.getValue();
                worker.loop();
            }
            completeBalanceTransaction();
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void completeBalanceTransaction() {
        JsonObject response = BankServices.bankService.listWaitingTransaction();
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonArray data = response.getAsJsonArray("data");
            for(int i = 0; i < data.size(); i++){
                JsonObject json = data.get(i).getAsJsonObject();
                BankTransaction transaction = new BankTransaction(json);
                completeBalanceTransaction(transaction);
            }
        }
    }

    private void completeBalanceTransaction(BankTransaction transaction) {
        AoeBankMessage message = AoeBankMessage.createFromBalanceTransaction(transaction);
        if(message == null){
            BankServices.bankService.updateBankState(transaction.id, BankTransactionState.ERROR);
            return;
        }
        Aoe
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

    public void removeBankAccount(BankAccount account) {
        bankWorkers.remove(account.id);
    }
}
