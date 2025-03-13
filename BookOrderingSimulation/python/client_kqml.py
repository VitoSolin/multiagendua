#!/usr/bin/env python
# -*- coding: utf-8 -*-

import socket
import time
import sys

def main():
    """
    Program client Python yang berkomunikasi dengan agen JADE KQML melalui socket.
    """
    HOST = 'localhost'  # IP Server JADE
    PORT = 5556         # Port yang dibuka oleh agen JADE KQML

    print("Python client for JADE Book Ordering System (KQML Protocol)")
    print("=========================================================")

    try:
        # Mencoba koneksi ke agen JADE KQML
        print(f"Connecting to JADE KQML agent at {HOST}:{PORT}...")
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((HOST, PORT))
        print("Connected to JADE KQML agent!")

        # Loop interaksi user
        while True:
            print("\nMenu:")
            print("1. Ask-if Book Availability (ASK_IF)")
            print("2. Order a Book (ACHIEVE)")
            print("3. Exit")
            
            choice = input("Select an option (1-3): ")
            
            if choice == '1':
                # Kirim permintaan ketersediaan buku (ask-if)
                print("Sending ASK_IF message to check book availability...")
                client_socket.sendall(b"ASK_IF: Apakah buku tersedia?\n")
                
                # Terima respons dari agen JADE
                response = client_socket.recv(1024).decode('utf-8')
                print(f"Response from JADE KQML agent: {response}")
                
            elif choice == '2':
                # Kirim permintaan pemesanan buku (achieve)
                print("Sending ACHIEVE message to order a book...")
                client_socket.sendall(b"ACHIEVE: Saya ingin membeli buku.\n")
                
                # Terima konfirmasi dari agen JADE
                response = client_socket.recv(1024).decode('utf-8')
                print(f"Response from JADE KQML agent: {response}")
                
            elif choice == '3':
                print("Exiting program...")
                break
                
            else:
                print("Invalid option. Please select 1, 2, or 3.")
        
    except ConnectionRefusedError:
        print("Error: Could not connect to the JADE KQML agent.")
        print("Make sure the JADE platform is running with the SocketSellerAgentKQML.")
        sys.exit(1)
    except Exception as e:
        print(f"An error occurred: {e}")
        sys.exit(1)
    finally:
        # Tutup koneksi
        if 'client_socket' in locals():
            client_socket.close()
            print("Socket connection closed.")

if __name__ == "__main__":
    main() 