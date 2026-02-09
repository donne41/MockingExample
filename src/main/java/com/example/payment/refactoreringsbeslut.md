Lagg till fält för paymentapi, databas och emailservice
för att enklare kunna byta ut dessa under testning och vidare utveckling.
Flyttat ut email från att vara hårdkodad till ett fält för att enklare kunna ändra vart betalningsbekräftelsen ska till.

refactorerat minimalt så att det använder fälten i koden istället för direkt kalla på klasser.
Enklare för testning och underhållning av koden.

Satt att databasinterfacet tar Prepared statement istället för en sträng av säkerhetsskäl. Det blir ytterligare något
man kan refaktorera till databas service klassen för att göra prepared statement men för stunden får den vara kvar i
paymenProcessor. 