def replace_simple(replacement):
	return lambda text, index: replacement

def replace_backslash(text, index):
	if index + 1 < len(text):
		next = text[index + 1]

		if next == '"': # \"
			return ",,"
		elif next == "n": # \n
			return "\\n"

	print(f"Special handling fell back to the default case. Tried to replace \ at index {index}: '{text}'")
	return "\\"

def replace_percent_sign(text, index):
	if index + 1 < len(text):
		next = text[index + 1]

		if next == "s": # %s
			return "%s"
		elif next.isnumeric() and text[index + 2] == "$" and text[index + 3] == "s": # %1$s, %2$s, ...
			return text[index:index + 4]

	print(f"Special handling fell back to the default case. Tried to replace % at index {index}: '{text}'")
	return "%"

def preprocess(lines):
	max = len(lines) - 1

	for line_i in range(1, max):
		line = lines[line_i]
		count = line.count("%s")

		if count <= 1:
			continue

		for count in range(1, count + 1):
			line = line.replace("%s", f"%{count}$s", 1)

		lines[line_i] = line

replacements = {
	"A": replace_simple("Ɐ"),
	"B": replace_simple("ᗺ"),
	"C": replace_simple("Ɔ"),
	"D": replace_simple("ᗡ"),
	"E": replace_simple("Ǝ"),
	"F": replace_simple("Ⅎ"),
	"G": replace_simple("⅁"),
	"H": replace_simple("H"),
	"I": replace_simple("I"),
	"J": replace_simple("Ր"),
	"K": replace_simple("Ʞ"),
	"L": replace_simple("Ꞁ"),
	"M": replace_simple("W"),
	"N": replace_simple("N"),
	"O": replace_simple("O"),
	"P": replace_simple("Ԁ"),
	"Q": replace_simple("Ꝺ"),
	"R": replace_simple("ᴚ"),
	"S": replace_simple("S"),
	"T": replace_simple("⟘"),
	"U": replace_simple("∩"),
	"V": replace_simple("Ʌ"),
	"W": replace_simple("M"),
	"X": replace_simple("X"),
	"Y": replace_simple("⅄"),
	"Z": replace_simple("Z"),
	"a": replace_simple("ɐ"),
	"b": replace_simple("q"),
	"c": replace_simple("ɔ"),
	"d": replace_simple("p"),
	"e": replace_simple("ǝ"),
	"f": replace_simple("ɟ"),
	"g": replace_simple("ᵷ"),
	"h": replace_simple("ɥ"),
	"i": replace_simple("ᴉ"),
	"j": replace_simple("ɾ"),
	"k": replace_simple("ʞ"),
	"l": replace_simple("ꞁ"),
	"m": replace_simple("ɯ"),
	"n": replace_simple("u"),
	"o": replace_simple("o"),
	"p": replace_simple("d"),
	"q": replace_simple("b"),
	"r": replace_simple("ɹ"),
	"s": replace_simple("s"),
	"t": replace_simple("ʇ"),
	"u": replace_simple("n"),
	"v": replace_simple("ʌ"),
	"w": replace_simple("ʍ"),
	"x": replace_simple("x"),
	"y": replace_simple("ʎ"),
	"z": replace_simple("z"),
	"0": replace_simple("0"),
	"1": replace_simple("⥝"),
	"2": replace_simple("ᘔ"),
	"3": replace_simple("Ɛ"),
	"4": replace_simple("߈"),
	"5": replace_simple("ϛ"),
	"6": replace_simple("9"),
	"7": replace_simple("ㄥ"),
	"8": replace_simple("8"),
	"9": replace_simple("6"),
    "'": replace_simple(","),
    ",": replace_simple("'"),
    "=": replace_simple("="),
    "+": replace_simple("+"),
    "/": replace_simple("/"),
    "*": replace_simple("*"),
    "-": replace_simple("-"),
    "(": replace_simple(")"),
    ")": replace_simple("("),
    "[": replace_simple("]"),
    "]": replace_simple("["),
    "{": replace_simple("}"),
    "}": replace_simple("{"),
    ".": replace_simple("˙"),
    ";": replace_simple("⸵"),
    " ": replace_simple(" "),
    "?": replace_simple("¿"),
    "!": replace_simple("¡"),
    "&": replace_simple("⅋"),
    "_": replace_simple("‾"),
    "^": replace_simple("^"),
	"#": replace_simple("#"),
	":": replace_simple(":"),
	"<": replace_simple(">"),
	">": replace_simple("<"),
    "\\": replace_backslash,
    "%": replace_percent_sign
}

with open("src/main/resources/assets/securitycraft/lang/en_us.json", encoding="UTF-8") as file:
	lines = file.readlines()
	preprocess(lines)
	upside_down_lines = []
	max = len(lines) - 1

	upside_down_lines.append("{\n")

	for line_i in range(1, max):
		split = lines[line_i].split('": "')
		text_i = 0

		# The last line needs to be special-cased, because it ends with " instead of ",
		if split[1].endswith('",\n'):
			text = split[1][:-3]
			line = '",\n'
		else:
			text = split[1][:-2]
			line = '"\n'

		while text_i < len(text):
			replacement = replacements[text[text_i]](text, text_i)
			line = replacement + line
			text_i += len(replacement)

		upside_down_lines.append(f'{split[0]}": "{line}')

	upside_down_lines.append("}")

with open("src/main/resources/assets/securitycraft/lang/en_ud.json", "w", encoding="UTF-8") as file:
	for line in upside_down_lines:
		file.write(line)