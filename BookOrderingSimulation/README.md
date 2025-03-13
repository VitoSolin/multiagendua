# Simulasi Protokol Komunikasi Antar Agen menggunakan FIPA-ACL dan KQML

Proyek ini mensimulasikan komunikasi antar agen menggunakan dua protokol komunikasi: FIPA-ACL dan KQML. Implementasi menggunakan JADE (Java Agent Development Framework) dengan integrasi Python.

## Checklist Tugas

### Setup dan Konfigurasi
- [x] Setup JADE di Java (termasuk konfigurasi classpath dan library)
- [x] Membuat file konfigurasi JADE

### Implementasi JADE (Java)
- [x] Implementasi BuyerAgent menggunakan protokol FIPA-ACL
- [x] Implementasi SellerAgent menggunakan protokol FIPA-ACL
- [x] Implementasi BuyerAgentKQML menggunakan protokol KQML
- [x] Implementasi SellerAgentKQML menggunakan protokol KQML

### Protokol FIPA-ACL
- [x] Implementasi skema komunikasi:
  - [x] Pembeli -> Penjual: mengirim request untuk informasi buku
  - [x] Penjual -> Pembeli: mengirim inform dengan detail buku
  - [x] Pembeli -> Penjual: mengirim request untuk membeli
  - [x] Penjual -> Pembeli: mengirim confirm jika pesanan berhasil

### Protokol KQML
- [x] Implementasi skema komunikasi:
  - [x] Pembeli -> Penjual: ask-if ketersediaan buku
  - [x] Penjual -> Pembeli: tell status ketersediaan
  - [x] Pembeli -> Penjual: achieve untuk membeli buku
  - [x] Penjual -> Pembeli: reply konfirmasi pemesanan

### Integrasi Python
- [x] Membuat program Python untuk berinteraksi dengan agen JADE
- [x] Implementasi komunikasi menggunakan socket atau Py4J
- [x] Mengirim pesan dari Python ke agen JADE
- [x] Menerima respon dari agen JADE di Python

### Pengujian
- [x] Menjalankan platform JADE dengan agen Java
- [x] Menjalankan program Python dan menguji komunikasi
- [x] Memverifikasi alur komunikasi sesuai dengan protokol

## Cara Penggunaan

### Menjalankan Agen JADE
1. Menjalankan JADE container menggunakan file batch standar:
   ```
   runjade.bat
   ```

2. Menjalankan JADE dengan agen yang mendukung socket (untuk komunikasi dengan Python):
   ```
   runsocketagents.bat
   ```

3. Menjalankan agen secara manual:
   ```
   java jade.Boot -gui buyer:fipaacl.BuyerAgent seller:fipaacl.SellerAgent
   ```
   
   atau untuk KQML:
   ```
   java jade.Boot -gui buyer:KMQL.BuyerAgentKQML seller:KMQL.SellerAgentKQML
   ```

### Menjalankan Program Python
1. Untuk berkomunikasi dengan agen FIPA-ACL (port 5555):
   ```
   python python/client.py
   ```

2. Untuk berkomunikasi dengan agen KQML (port 5556):
   ```
   python python/client_kqml.py
   ```

## Struktur Proyek
- `src/fipaacl/` - Implementasi agen dengan protokol FIPA-ACL
- `src/KMQL/` - Implementasi agen dengan protokol KQML
- `lib/` - Library JADE dan dependensi lainnya
- `python/` - Kode Python untuk integrasi dengan JADE
  - `client.py` - Client Python untuk berkomunikasi dengan agen FIPA-ACL
  - `client_kqml.py` - Client Python untuk berkomunikasi dengan agen KQML

## Alur Komunikasi

### FIPA-ACL dengan Socket
1. Python Client mengirim `REQUEST_INFO` ke SocketSellerAgent
2. SocketSellerAgent mengirim informasi buku kembali ke Python Client
3. Python Client mengirim `ORDER_BOOK` ke SocketSellerAgent
4. SocketSellerAgent mengirim konfirmasi pemesanan kembali ke Python Client

### KQML dengan Socket
1. Python Client mengirim `ASK_IF` ke SocketSellerAgentKQML
2. SocketSellerAgentKQML mengirim `TELL` tentang ketersediaan buku
3. Python Client mengirim `ACHIEVE` untuk memesan buku
4. SocketSellerAgentKQML mengirim `REPLY` dengan konfirmasi pemesanan 