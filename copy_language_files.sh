#!/bin/bash
cd src/main/resources/assets/securitycraft/lang

declare -A languages=(
	["de"]="at ch"
	["es"]="ar cl mx uy ve"
	["fr"]="ca"
)

for standard in "${!languages[@]}"; do
	declare -a dialects=(${languages[$standard]})

	for dialect in "${dialects[@]}"; do
		cp "${standard}_${standard}.lang" "${standard}_${dialect}.lang"
	done
done