package com.nttdatabc.msmonedero.service;

import com.nttdatabc.msmonedero.config.KafkaConsumerListener;
import com.nttdatabc.msmonedero.model.MovementWallet;
import com.nttdatabc.msmonedero.repository.MovementWalletRepository;
import com.nttdatabc.msmonedero.repository.WalletRepository;
import com.nttdatabc.msmonedero.service.interfaces.MovementWalletService;
import com.nttdatabc.msmonedero.service.interfaces.WalletService;
import com.nttdatabc.msmonedero.service.strategy.insert_movement.ContextInsert;
import com.nttdatabc.msmonedero.service.strategy.insert_movement.InsertDebitCard;
import com.nttdatabc.msmonedero.service.strategy.insert_movement.InsertDocument;
import com.nttdatabc.msmonedero.utils.Utilitarios;
import com.nttdatabc.msmonedero.utils.exceptions.errors.ErrorResponseException;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.nttdatabc.msmonedero.utils.Constantes.EX_ERROR_BALANCE_INSUFICIENT;
import static com.nttdatabc.msmonedero.utils.MovementValidation.validateCentralFacade;
import static com.nttdatabc.msmonedero.utils.Utilitarios.generateUuid;

@Service
public class MovementWalletServiceImpl implements MovementWalletService {
  @Autowired
  private MovementWalletRepository movementWalletRepository;
  @Autowired
  private WalletRepository walletRepository;
  @Autowired
  private WalletService walletService;
  @Autowired
  private KafkaConsumerListener kafkaConsumerListener;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Override
  public Mono<Void> createMovementWalletservice(MovementWallet movementWallet) {
    return validateCentralFacade(movementWallet, walletRepository)
        .then(walletService.getWalletByIdService(movementWallet.getWalletId()))
        .flatMap(walletFlujo -> {
          ContextInsert ctxInsert = null;
          if(walletFlujo.getCardDebitAssociate() == null){ // DNI
              ctxInsert = new ContextInsert(new InsertDocument());
          }else{ // associate DebitCard
            ctxInsert = new ContextInsert(new InsertDebitCard());
          }
          return ctxInsert.executeInsert(walletFlujo, movementWallet,movementWalletRepository,
              walletRepository, kafkaTemplate, kafkaConsumerListener);
        });
  }
}
