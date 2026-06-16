#!/bin/bash
cd src/main/resources/assets/securitycraft/lang

declare -A languages=(
	["de"]="de_at de_ch bar fra_de ksh nds_de sxu"
	["es"]="es_ar es_cl es_ec es_mx es_uy es_ve"
	["fr"]="fr_ca"
	["nl"]="nl_be"
)

for standard in "${!languages[@]}"; do
	declare -a dialects=(${languages[$standard]})

	for dialect in "${dialects[@]}"; do
		cp "${standard}_${standard}.json" "${dialect}.json"
	done
done