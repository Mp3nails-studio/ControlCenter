# Control Center (Android)

Panel szybkich ustawien w stylu iOS, wysuwany z gory ekranu nad kazda aplikacja (jak w One UI).
Kotlin + Jetpack Compose. Ikony: Material Icons Extended. Rozmycie tla: systemowe (Android 12+).

## Jak zbudowac
1. Zainstaluj Android Studio (najnowsze).
2. File > Open > wybierz folder `ControlCenter`. Poczekaj az Gradle sie zsynchronizuje.
3. Podlacz telefon (wlaczone debugowanie USB) i kliknij Run, albo
   Build > Build Bundle(s) / APK(s) > Build APK(s), a potem zainstaluj plik APK.

## Pierwsze uruchomienie
Otworz aplikacje i nadaj uprawnienia (zielone ptaszki):
- Wyswietlanie nad innymi aplikacjami - wymagane
- Zmiana ustawien systemu - jasnosc i blokada obrotu
- Dostep do powiadomien - odczyt grajacej muzyki
- Nie przeszkadzac - przelacznik DND
Potem wlacz przelacznik "Wlacz panel". Przesun palcem w dol przy gornej krawedzi ekranu (prawa strona).
Strefe gestu ustawisz suwakami, mozesz tez wlaczyc czerwony pasek, zeby ja zobaczyc.

## Co dziala bezposrednio
Jasnosc, glosnosc, wyciszenie, tryb dzwonka, latarka, blokada obrotu, Nie przeszkadzac, sterowanie muzyka.

## Co otwiera ekran systemu
Od Androida 10 zwykla aplikacja nie moze sama przelaczyc Wi-Fi, Bluetooth, trybu samolotowego
ani danych komorkowych. Przyciski pokazuja aktualny stan i otwieraja systemowy panel/ustawienia.

## Znane ograniczenia
- Nie sprawdzalem budowania w Android Studio w tym srodowisku - jesli Gradle zglosi blad, wklej go do czatu.
- Na niektorych telefonach (np. z wlaczona oszczednoscia baterii) system moze zabijac serwis -
  wylacz optymalizacje baterii dla aplikacji.
- Rozmycie dziala na Androidzie 12+ i gdy w systemie nie jest wylaczone (tryb oszczedzania baterii je wylacza).
