tabla = []
final = []
alias = "Pame"
nuevo = 87 
file = open("./puntajes.txt", 'r')
for line in file.readlines():
	dic = {"name":line.split()[0], "puntaje":line.split()[1]}
	tabla.append(dic)
cont = 0
newUs = {"name":alias, "puntaje":str(nuevo)}
if len(tabla) == 0:
	tabla.append(newUs)
else:
	for x in tabla:
		if int(x["puntaje"]) < nuevo:
			tabla.insert(cont, {"name":alias, "puntaje":str(nuevo)})
			break
		cont+=1
file_u = open("./puntajes2.txt", 'w')
for x in tabla:
	aux = x["name"]+" "+x["puntaje"]+"\n"
	final.append(aux)
file_u.writelines(final)

