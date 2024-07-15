languages = ["de_de", "en_gb", "en_us", "es_es", "fr_fr", "it_it", "ru_ru", "zh_cn"]

for language in languages:
	file_name = "src/main/resources/assets/securitycraft/lang/" + language + ".lang"

	with open(file_name, encoding="UTF-8") as file:
		lines = file.readlines()
		lines.sort()

	with open(file_name, "w", encoding="UTF-8") as file:
		for line in lines:
			file.write(line)