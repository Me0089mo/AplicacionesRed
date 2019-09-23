import socket
import struct
import sys
import random
import struct
import time

class Server:
	def __init__(self): 
		self.host = input("Ingrese la dirección IP del servidor: ")
		self.puerto = int(input("Ingrese el puerto en el que se conectará el servidor: "))
		self.s=socket.socket()
		self.cl_socket = None
		self.s.bind((self.host, self.puerto))

	def listen(self):
		self.s.listen(1)
		print("Esperando conexion...")  
		self.cl_socket, cl_ip = self.s.accept()
  
	def set_difficulty(self):
		cont=0
		aux = bytes()
		while cont<4:
			recibido = self.cl_socket.recv(1024)
			aux = aux + recibido
			cont+=1
		level = struct.unpack('>i', aux)
		self.cl_socket.close()
		return level[0]

	def generate_map(self, difficulty):
		mapa_minas = []
		random.seed(None)
		if difficulty == 1:
			no_minas = 10
			no_casillas = 81
		elif difficulty == 2:
			no_minas = 40
			no_casillas = 256
		else:
			no_minas = 99
			no_casillas = 480
		for i in range(no_minas):
			mina = random.randint(1,no_casillas)
			fin = mina.to_bytes(4,  byteorder="big")
			self.cl_socket.sendall(fin)
		self.cl_socket.close
	
	def puntuationRecord(self, difficulty):
		cont=0
		aux = bytes()
		alias_recv = self.cl_socket.recv(1024)
		alias = str(alias_recv[2:len(alias_recv)], 'ascii')
		print(alias)
		#while cont<4:
		puntaje = self.cl_socket.recv(1024)
		aux = aux + puntaje
		#cont+=1
		punt = struct.unpack('>i', aux)
		print(punt[0])
		self.saveRecords(alias, punt[0], difficulty)
		self.cl_socket.close()

	def saveRecords(self, alias, puntaje, difficulty):
		tabla = []
		final = []
		if difficulty == 1:
			fileName = "./puntajesFacil.txt"
		elif difficulty == 2:
			fileName = "./puntajesIntermedio.txt"
		else:
			fileName = "./puntajesExperto.txt"
		#Obteniendo el archivo de puntajes
		file = open(fileName, 'r')
		for line in file.readlines():
			dic = {"name":line.split()[0], "puntaje":line.split()[1]}
			tabla.append(dic)
		file.close()
		#Insertando el nuevo puntaje
		cont = 0
		newUs = {"name":alias, "puntaje":str(puntaje)}
		numPuntajes = len(tabla) 
		if  numPuntajes == 0:
			tabla.append(newUs)
		else:
			for x in tabla:
				if int(x["puntaje"]) < puntaje:
					tabla.insert(cont, newUs)
					break
				cont+=1
		if len(tabla) == numPuntajes:
			tabla.append(newUs)
		if len(tabla) > 10:
			tabla.pop()
		#Enviando los puntajes
		tamTabla = len(tabla).to_bytes(4,  byteorder="big")
		self.cl_socket.sendall(tamTabla)
		for x in tabla:
			self.cl_socket.sendall(bytes(x["name"], 'utf-8'))
			punt_env = int(x["puntaje"]).to_bytes(4,  byteorder="big")
			print(punt_env)
			self.cl_socket.sendall(punt_env)
		#Actualizando el archivo de puntajes
		file_u = open(fileName, 'w')
		for x in tabla:
			aux = x["name"]+" "+x["puntaje"]+"\n"
			final.append(aux)
		file_u.writelines(final)
		file_u.close()
		print(final)

	#def chargeRecords(self):


server = Server()
while True:
	server.listen()
	dif = server.set_difficulty()
	server.listen()  
	server.generate_map(dif)
	server.listen()
	server.puntuationRecord(dif)
#sc.close()  
#s.close()  