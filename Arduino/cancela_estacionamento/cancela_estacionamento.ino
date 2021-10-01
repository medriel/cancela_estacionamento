#include <Servo.h>

Servo servo_9;
const int pino_sensor(5); //13
const int pino(2); //12
int valor_sensor;

void setup(){
  Serial.begin(2000000);
  servo_9.attach(9, 500, 2500);
  pinMode(pino_sensor,INPUT_PULLUP);
  pinMode(pino, OUTPUT);
}

void tratar_fechar_cancela(){
  delay(100);
  
  int cont_tempo = 0;

  for(cont_tempo;cont_tempo<50;cont_tempo++){
    valor_sensor = digitalRead(pino_sensor);
    if(valor_sensor == HIGH){
      digitalWrite(pino,HIGH);
      servo_9.write(90);
      cont_tempo = 0;
      delay(100);
    }
    delay(100);
  }
  Serial.println("Fechado");
  digitalWrite(pino,LOW);
}

void loop(){
  
  valor_sensor = digitalRead(pino_sensor);
  
    if(valor_sensor == HIGH){
      Serial.println("Aberto");
      digitalWrite(pino,HIGH);
      servo_9.write(90);
      tratar_fechar_cancela();
    }
  
  if(valor_sensor == LOW){
      Serial.println("Fechado");
      digitalWrite(pino,LOW);
      servo_9.write(0);
    }

    delay(1000);
}
