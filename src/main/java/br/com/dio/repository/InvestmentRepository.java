package br.com.dio.repository;


import br.com.dio.expcetion.AccountWithInvestmentException;
import br.com.dio.expcetion.InvestmentNotFoundException;
import br.com.dio.expcetion.PixInUseException;
import br.com.dio.expcetion.WalletNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.Investiment;
import br.com.dio.model.InvestimentWallet;

import java.util.ArrayList;
import java.util.List;

import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class InvestmentRepository {

    private long nextId = 0;
    private final List<Investiment> investments = new ArrayList<>();
    private final List<InvestimentWallet> wallets = new ArrayList<>();



    public Investiment create(final long tax, final long initialFunds){
        this.nextId++;
        var investment = new Investiment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }


    public InvestimentWallet initInvestment(final AccountWallet account, final long id) {

        var accountInUse = wallets.stream().map(a -> a.getAccount()).toList();
        if(!wallets.isEmpty()) {
            if (accountInUse.contains(account)) {
                throw new AccountWithInvestmentException("A conta '" + account + "' já possui um investimento");
            }
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFounds());
        var wallet = new InvestimentWallet(investment, account, investment.initialFounds());
        wallets.add(wallet);
        return wallet;
    }


    public InvestimentWallet deposit(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimento");
        return wallet;
    }


    public InvestimentWallet withdraw(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount().addMoney(wallet.reduceMoney(funds), wallet.getService(), "Saque de investimentos");
        if(wallet.getFunds() == 0){
            wallets.remove(wallet);
        }
        return wallet;
    }

    public void updateAmount(){
        wallets.forEach(w -> w.updateAmount(w.getInvestiment().tax()));
    }

    public Investiment findById(final long id){
        return investments.stream().filter(a -> a.id() == id).findFirst().
                orElseThrow(()-> new InvestmentNotFoundException("O investimento '"+id+"' não foi encontrado"));
    }

    public InvestimentWallet findWalletByAccountPix(final String pix){
        return wallets.stream().filter(w -> w.getAccount().getPix().
                contains(pix)).findFirst().orElseThrow(()-> new WalletNotFoundException("A carteira não foi encontrada"));
    }


    public List<InvestimentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investiment> list(){
        return this.investments;
    }

}
