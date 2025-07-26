def replace_single(replacement):
	return lambda text, index: (1, replacement)

def replace_backslash(text, index):
	if index + 1 < len(text):
		next = text[index + 1]

		if next == "n": # \n
			return (2, "\\n")

	print(f"Special handling fell back to the default case. Tried to replace \ at index {index}: '{text}'")
	return (1, "\\")

def replace_percent_sign(text, index):
	if index + 1 < len(text):
		next = text[index + 1]

		if next == "s": # %s
			return (2, "%s")
		elif next.isnumeric() and text[index + 2] == "$" and text[index + 3] == "s": # %1$s, %2$s, ...
			return (4, text[index:index + 4])

	print(f"Special handling fell back to the default case. Tried to replace % at index {index}: '{text}'")
	return (1, "%")

def preprocess(lines):
	max = len(lines) - 1

	for line_i in range(1, max):
		line = lines[line_i]
		count = line.count("%s")

		if count > 1:
			for count in range(1, count + 1):
				line = line.replace("%s", f"%{count}$s", 1)

		lines[line_i] = line.replace("=", "🟰").replace("🟰", "=", 1)

replacements = {
	"A": replace_single("Ɐ"),
	"B": replace_single("ᗺ"),
	"C": replace_single("Ɔ"),
	"D": replace_single("ᗡ"),
	"E": replace_single("Ǝ"),
	"F": replace_single("Ⅎ"),
	"G": replace_single("⅁"),
	"H": replace_single("H"),
	"I": replace_single("I"),
	"J": replace_single("Ր"),
	"K": replace_single("Ʞ"),
	"L": replace_single("Ꞁ"),
	"M": replace_single("W"),
	"N": replace_single("N"),
	"O": replace_single("O"),
	"P": replace_single("Ԁ"),
	"Q": replace_single("Ꝺ"),
	"R": replace_single("ᴚ"),
	"S": replace_single("S"),
	"T": replace_single("⟘"),
	"U": replace_single("∩"),
	"V": replace_single("Ʌ"),
	"W": replace_single("M"),
	"X": replace_single("X"),
	"Y": replace_single("⅄"),
	"Z": replace_single("Z"),
	"a": replace_single("ɐ"),
	"b": replace_single("q"),
	"c": replace_single("ɔ"),
	"d": replace_single("p"),
	"e": replace_single("ǝ"),
	"f": replace_single("ɟ"),
	"g": replace_single("ᵷ"),
	"h": replace_single("ɥ"),
	"i": replace_single("ᴉ"),
	"j": replace_single("ɾ"),
	"k": replace_single("ʞ"),
	"l": replace_single("ꞁ"),
	"m": replace_single("ɯ"),
	"n": replace_single("u"),
	"o": replace_single("o"),
	"p": replace_single("d"),
	"q": replace_single("b"),
	"r": replace_single("ɹ"),
	"s": replace_single("s"),
	"t": replace_single("ʇ"),
	"u": replace_single("n"),
	"v": replace_single("ʌ"),
	"w": replace_single("ʍ"),
	"x": replace_single("x"),
	"y": replace_single("ʎ"),
	"z": replace_single("z"),
	"0": replace_single("0"),
	"1": replace_single("⥝"),
	"2": replace_single("ᘔ"),
	"3": replace_single("Ɛ"),
	"4": replace_single("߈"),
	"5": replace_single("ϛ"),
	"6": replace_single("9"),
	"7": replace_single("ㄥ"),
	"8": replace_single("8"),
	"9": replace_single("6"),
    "'": replace_single(","),
    ",": replace_single("'"),
    "🟰": replace_single("="),
    "+": replace_single("+"),
    "/": replace_single("/"),
    "*": replace_single("*"),
    "-": replace_single("-"),
    "(": replace_single(")"),
    ")": replace_single("("),
    "[": replace_single("]"),
    "]": replace_single("["),
    "{": replace_single("}"),
    "}": replace_single("{"),
    ".": replace_single("˙"),
    ";": replace_single("⸵"),
    " ": replace_single(" "),
    "?": replace_single("¿"),
    "!": replace_single("¡"),
    "&": replace_single("⅋"),
    "_": replace_single("‾"),
    "^": replace_single("^"),
	"#": replace_single("#"),
	":": replace_single(":"),
	"<": replace_single(">"),
	">": replace_single("<"),
	'"': replace_single(",,"),
	"|": replace_single("|"),
    "\\": replace_backslash,
    "%": replace_percent_sign
}

with open("src/main/resources/assets/securitycraft/lang/en_us.lang", encoding="UTF-8") as file:
	lines = file.readlines()
	preprocess(lines)
	upside_down_lines = []
	max = len(lines) - 1

	for line_i in range(1, max):
		split = lines[line_i].split("=")
		text = split[1][:-1]
		text_i = 0
		line = "\n"

		while text_i < len(text):
			replacement = replacements[text[text_i]](text, text_i)
			line = replacement[1] + line
			text_i += replacement[0]

		upside_down_lines.append(f"{split[0]}={line}")

with open("src/main/resources/assets/securitycraft/lang/en_ud.lang", "w", encoding="UTF-8") as file:
	for line in upside_down_lines:
		file.write(line)