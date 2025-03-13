#!/usr/bin/env python
# -*- coding: utf-8 -*-

import socket
import time
import sys

def main():
    """
    Program client Python yang berkomunikasi dengan agen JADE melalui socket.
    """
    HOST = 'localhost'  # IP Server JADE
    PORT = 5555         # Port yang dibuka oleh agen JADE

    print("Python client for JADE Book Ordering System")
    print("===========================================")

    try:
        # Mencoba koneksi ke agen JADE
        print(f"Connecting to JADE agent at {HOST}:{PORT}...")
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.connect((HOST, PORT))
        print("Connected to JADE agent!")

        # Loop interaksi user
        while True:
            print("\nMenu:")
            print("1. Request Book Information")
            print("2. Order a Book")
            print("3. Exit")
            
            choice = input("Select an option (1-3): ")
            
            if choice == '1':
                # Kirim permintaan informasi buku
                print("Requesting book information...")
                client_socket.sendall(b"REQUEST_INFO\n")
                
                # Terima respons dari agen JADE
                response = client_socket.recv(1024).decode('utf-8')
                print(f"Response from JADE agent: {response}")
                
            elif choice == '2':
                # Kirim permintaan pemesanan buku
                print("Ordering a book...")
                client_socket.sendall(b"ORDER_BOOK\n")
                
                # Terima konfirmasi dari agen JADE
                response = client_socket.recv(1024).decode('utf-8')
                print(f"Response from JADE agent: {response}")
                
            elif choice == '3':
                print("Exiting program...")
                break
                
            else:
                print("Invalid option. Please select 1, 2, or 3.")
        
    except ConnectionRefusedError:
        print("Error: Could not connect to the JADE agent.")
        print("Make sure the JADE platform is running with the SocketSellerAgent.")
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