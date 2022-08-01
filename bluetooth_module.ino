const int ledPin6 = 6;
const int ledPin8 = 8;
const int ledPin10 = 10;
const int ledPin12 = 12;

char Incoming_value = '0';


void setup() {
  Serial.begin(9600);
  pinMode(ledPin6, OUTPUT);
  pinMode(ledPin8, OUTPUT);
  pinMode(ledPin10, OUTPUT);
  pinMode(ledPin12, OUTPUT);

}

void loop() {
  int delayPeriod = 100;

  while(Serial.available() > 0)  
  {
      Incoming_value = Serial.read();
      Serial.print(Incoming_value);        
      Serial.print("\n"); 
      
      switch(Incoming_value){
        case '1':
            digitalWrite(ledPin6, HIGH);  
            digitalWrite(ledPin8, HIGH);  
            digitalWrite(ledPin10, HIGH);  
            digitalWrite(ledPin12, HIGH); 
         break;
         
        case '0':
            digitalWrite(ledPin6, LOW);  
            digitalWrite(ledPin8, LOW);
            digitalWrite(ledPin10, LOW);  
            digitalWrite(ledPin12, LOW);
         break; 

         case '2':
            digitalWrite(ledPin6, HIGH);  
            digitalWrite(ledPin8, HIGH);  
            digitalWrite(ledPin10, LOW);  
            digitalWrite(ledPin12, LOW);
         break;

         case '3':
            digitalWrite(ledPin6, LOW);  
            digitalWrite(ledPin8, LOW);
            digitalWrite(ledPin10, HIGH);  
            digitalWrite(ledPin12, HIGH); 
         break; 
    }
  }
}

void trunOnOff6(){
    int delayPeriod = 100;
    digitalWrite(ledPin6, HIGH);  
    delay(delayPeriod);
    digitalWrite(ledPin6, LOW);
}


void trunOnOff8(){
    int delayPeriod = 100;
    digitalWrite(ledPin8, HIGH);  
    delay(delayPeriod);
    digitalWrite(ledPin8, LOW);
}


void trunOnOff10(){
    int delayPeriod = 100;
    digitalWrite(ledPin10, HIGH);  
    delay(delayPeriod);
    digitalWrite(ledPin10, LOW);
}



void trunOnOff12(){
    int delayPeriod = 100;
    digitalWrite(ledPin12, HIGH);  
    delay(delayPeriod);
    digitalWrite(ledPin12, LOW);
}
