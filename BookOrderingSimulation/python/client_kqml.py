#!/usr/bin/env python
# -*- coding: utf-8 -*-

import socket
import time
import sys
import os

def check_port(host, port):
    """Check if a port is open on the host"""
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        result = sock.connect_ex((host, port))
        return result == 0
    finally:
        sock.close()

def main():
    """
    Program client Python yang berkomunikasi dengan agen JADE KQML melalui socket.
    """
    HOST = 'localhost'  # IP Server JADE
    PORT = 5556         # Port yang dibuka oleh agen JADE KQML

    print("Python client for JADE Book Ordering System (KQML Protocol)")
    print("=========================================================")
    print(f"Current working directory: {os.getcwd()}")
    print(f"Checking connection to {HOST}:{PORT}...")

    try:
        # Tunggu sebentar untuk memastikan server sudah siap
        print("Waiting for server to be ready...")
        time.sleep(2)
        
        # Check if port is open
        if not check_port(HOST, PORT):
            print(f"Error: Port {PORT} is not open on {HOST}")
            print("Please make sure:")
            print("1. JADE platform is running with SocketSellerAgentKQML")
            print("2. No firewall is blocking the connection")
            print("3. The agent has successfully initialized its socket server")
            sys.exit(1)
        
        # Mencoba koneksi ke agen JADE KQML
        print(f"Connecting to JADE KQML agent at {HOST}:{PORT}...")
        client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        client_socket.settimeout(5)  # Set timeout to 5 seconds
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
    except socket.timeout:
        print("Error: Connection timed out.")
        print("The server might be busy or not responding.")
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