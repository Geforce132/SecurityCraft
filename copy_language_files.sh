#!/bin/bash
cd src/main/resources/assets/securitycraft/lang

declare -A languages=(
	["de"]="at ch"
	["es"]="ar cl ec mx uy ve"
	["fr"]="ca"
)

for standard in "${!languages[@]}"; do
	declare -a dialects=(${languages[$standard]})

	for dialect in "${dialects[@]}"; do
		cp "${standard}_${standard}.json" "${standard}_${dialect}.json"
	done
done