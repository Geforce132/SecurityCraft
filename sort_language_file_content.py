languages = ["de_de", "en_gb", "en_us", "es_es", "fr_fr", "it_it"]

for language in languages:
	file_name = "src/main/resources/assets/securitycraft/lang/" + language + ".json"

	with open(file_name, encoding="UTF-8") as file:
		lines = file.readlines()
		lines.sort()

	with open(file_name, "w", encoding="UTF-8") as file:
		for line in lines:
			file.write(line)