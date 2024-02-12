package com.nttdatabc.msmonedero.service.strategy.insert_movement;

import com.nttdatabc.msmonedero.config.KafkaConsumerListener;
import com.nttdatabc.msmonedero.model.MovementWallet;
import com.nttdatabc.msmonedero.model.Wallet;
import com.nttdatabc.msmonedero.repository.MovementWalletRepository;
import com.nttdatabc.msmonedero.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

public class ContextInsert {
  private IStrategyInsertMovement iStrategyInsertMovement;

  public ContextInsert(IStrategyInsertMovement iStrategyInsertMovement) {
    this.iStrategyInsertMovement = iStrategyInsertMovement;
  }

  public Mono<Void>executeInsert(Wallet wallet, MovementWallet movementWallet,
                                 MovementWalletRepository movementWalletRepository,
                                 WalletRepository walletRepository, KafkaTemplate<String, String> kafkaTemplate,
                                 KafkaConsumerListener kafkaConsumerListener){
    return iStrategyInsertMovement.insert(wallet,movementWallet,
        movementWalletRepository,walletRepository, kafkaTemplate, kafkaConsumerListener);
  }
}
