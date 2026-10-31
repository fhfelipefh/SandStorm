import argparse
import json
import math
import os
import re
import sys
import numpy as np
from PIL import Image

BLOCKS = [
    {
        "id": "atmospheric_terraformer",
        "name": "Terraformador Atmosférico",
        "role": "Estabilização Climática Global",
        "model": "atmospheric_terraformer.json"
    },
    {
        "id": "acoustic_defense_pylon",
        "name": "Pilone de Defesa Acústica",
        "role": "Repulsor Sísmico Anti-Verme",
        "model": "acoustic_defense_pylon.json"
    },
    {
        "id": "stellar_singularity_reactor",
        "name": "Reator de Singularidade Estelar",
        "role": "Geração Hiper-Energética & WPT Global",
        "model": "stellar_singularity_reactor.json"
    },
    {
        "id": "bioreactor_vat",
        "name": "Vat Biorreator",
        "role": "Cultura de Biomassa & Enzimas",
        "model": "bioreactor_vat.json"
    },
    {
        "id": "bio_regeneration_pod",
        "name": "Cápsula de Biorregeneração",
        "role": "Cura Celular Rápida de Operadores",
        "model": "bio_regeneration_pod.json"
    },
    {
        "id": "auto_assembly_line",
        "name": "Linha de Montagem Robótica",
        "role": "Automação de Chassis & Circuitos",
        "model": "auto_assembly_line.json"
    },
    {
        "id": "quantum_network_controller",
        "name": "Controlador Quântico ME",
        "role": "Núcleo de Rede & Barramento de Dados",
        "model": "quantum_network_controller.json"
    },
    {
        "id": "quantum_disk_drive",
        "name": "Unidade de Discos Quânticos",
        "role": "Armazenamento Holográfico Massivo",
        "model": "quantum_disk_drive.json"
    },
    {
        "id": "quantum_access_terminal",
        "name": "Terminal de Acesso Quântico",
        "role": "Interface Gráfica de Logística ME",
        "model": "quantum_access_terminal.json"
    },
    {
        "id": "desalination_filter",
        "name": "Filtro Dessalinizador",
        "role": "Purificação de Água Salobra",
        "model": "desalination_filter.json"
    },
    {
        "id": "nanite_fabricator",
        "name": "Fabricador de Nanitas",
        "role": "Síntese Molecular de Componentes",
        "model": "nanite_fabricator.json"
    },
    {
        "id": "printer_3d",
        "name": "Impressora 3D de Precisão",
        "role": "Prototipagem Rápida de Peças",
        "model": "printer_3d.json"
    },
    {
        "id": "storm_wind_turbine",
        "name": "Turbina Eólica de Tempestade",
        "role": "Geração Elétrica Cinética Eólica",
        "model": "storm_wind_turbine.json"
    },
    {
        "id": "compact_slab_piston",
        "name": "Pistão Compacto de Lajes",
        "role": "Automação Redstone de 0.5 Bloco",
        "model": "compact_slab_piston.json"
    },
    {
        "id": "subspace_gateway",
        "name": "Portal Subespacial",
        "role": "Trânsito Quântico Dimensional",
        "model": "subspace_gateway.json"
    },
    {
        "id": "vault_door_controller",
        "name": "Controlador de Porta de Cofre",
        "role": "Acesso Hermético de Alta Segurança",
        "model": "vault_door_controller.json"
    },
    {
        "id": "deep_core_drill",
        "name": "Perfuratriz de Núcleo Profundo",
        "role": "Mineração Subterrânea Automatizada",
        "model": "deep_core_drill.json"
    },
    {
        "id": "electric_arc_furnace",
        "name": "Fornalha de Arco Elétrico",
        "role": "Metalurgia Avançada de Superligas",
        "model": "electric_arc_furnace.json"
    },
    {
        "id": "sandstone_crate",
        "name": "Caixote de Arenito Selado",
        "role": "Armazenamento Resistente a Tempestades",
        "model": "sandstone_crate.json"
    },
    {
        "id": "chemical_refinery",
        "name": "Refinaria Química",
        "role": "Fracionamento de Fluidos Fósseis",
        "model": "chemical_refinery.json"
    },
    {
        "id": "dew_condenser",
        "name": "Condensador de Orvalho",
        "role": "Captação Passiva de Umidade do Ar",
        "model": "dew_condenser.json"
    },
    {
        "id": "atmospheric_carbon_extractor",
        "name": "Extrator de Carbono",
        "role": "Captura Atmosférica de Polímeros",
        "model": "atmospheric_carbon_extractor.json"
    },
    {
        "id": "thermal_generator",
        "name": "Gerador Geotérmico",
        "role": "Conversão Térmica de Lava & Manto",
        "model": "thermal_generator.json"
    },
    {
        "id": "solid_state_accumulator",
        "name": "Acumulador de Estado Sólido",
        "role": "Armazenamento de Alta Tensão",
        "model": "solid_state_accumulator.json"
    },
    {
        "id": "hydroponic_chamber",
        "name": "Câmara Hidropônica",
        "role": "Cultivo Subterrâneo Otimizado",
        "model": "hydroponic_chamber.json"
    }
]

def resolve_texture_file(tex_key, textures_dict):
    while tex_key.startswith('#'):
        var_name = tex_key[1:]
        tex_key = textures_dict.get(var_name, '')
    if not tex_key:
        return None
    if tex_key.startswith('sandstorm:'):
        rel = tex_key.replace('sandstorm:', '')
        candidates = [
            f"src/main/resources/assets/sandstorm/{rel}.png",
            f"src/main/resources/assets/sandstorm/textures/{rel}.png",
            f"src/main/resources/assets/sandstorm/textures/block/{os.path.basename(rel)}.png"
        ]
        for c in candidates:
            if os.path.exists(c):
                return c
    return None

def get_block_elements(model_json_path):
    with open(model_json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    parent = data.get('parent', '')
    textures = data.get('textures', {})

    if 'elements' in data:
        return data['elements'], textures

    faces = {}
    if 'cube_all' in parent:
        for fn in ['up', 'down', 'north', 'south', 'east', 'west']:
            faces[fn] = {'texture': '#all'}
    elif 'cube_bottom_top' in parent:
        faces['up'] = {'texture': '#top'}
        faces['down'] = {'texture': '#bottom'}
        for fn in ['north', 'south', 'east', 'west']:
            faces[fn] = {'texture': '#side'}
    elif 'cube_column' in parent:
        faces['up'] = {'texture': '#end'}
        faces['down'] = {'texture': '#end'}
        for fn in ['north', 'south', 'east', 'west']:
            faces[fn] = {'texture': '#side'}
    elif 'orientable' in parent:
        faces['up'] = {'texture': '#top'}
        faces['down'] = {'texture': '#bottom' if 'bottom' in textures else '#top'}
        faces['north'] = {'texture': '#front'}
        faces['south'] = {'texture': '#side'}
        faces['east'] = {'texture': '#side'}
        faces['west'] = {'texture': '#side'}
    elif 'cube' in parent:
        for fn in ['up', 'down', 'north', 'south', 'east', 'west']:
            faces[fn] = {'texture': f'#{fn}'}
    else:
        first_tex = list(textures.keys())[0] if textures else 'all'
        for fn in ['up', 'down', 'north', 'south', 'east', 'west']:
            faces[fn] = {'texture': f'#{first_tex}'}

    element = {
        'from': [0, 0, 0],
        'to': [16, 16, 16],
        'faces': faces
    }
    return [element], textures

def render_block_isometric(model_json_path, out_path, size=300):
    elements, textures = get_block_elements(model_json_path)

    loaded_textures = {}
    def get_texture_arr(tex_ref):
        tf = resolve_texture_file(tex_ref, textures)
        if not tf or not os.path.exists(tf):
            arr = np.full((16, 16, 4), 180, dtype=np.uint8)
            arr[:, :, 3] = 255
            return arr
        if tf not in loaded_textures:
            img = Image.open(tf).convert('RGBA')
            loaded_textures[tf] = np.array(img)
        return loaded_textures[tf]

    # True isometric projection angle: 30° on both left and right edges, 90° center vertical
    yaw = math.radians(-135)
    pitch = math.atan(1.0 / math.sqrt(2.0))

    cos_y, sin_y = math.cos(yaw), math.sin(yaw)
    cos_p, sin_p = math.cos(pitch), math.sin(pitch)

    def world_to_cam(x, y, z):
        cx, cy, cz = x - 8.0, y - 8.0, z - 8.0
        x1 = cx * cos_y + cz * sin_y
        y1 = cy
        z1 = -cx * sin_y + cz * cos_y
        x2 = x1
        y2 = y1 * cos_p - z1 * sin_p
        z2 = y1 * sin_p + z1 * cos_p
        return x2, y2, z2

    def norm_to_cam(nx, ny, nz):
        x1 = nx * cos_y + nz * sin_y
        y1 = ny
        z1 = -nx * sin_y + nz * cos_y
        x2 = x1
        y2 = y1 * cos_p - z1 * sin_p
        z2 = y1 * sin_p + z1 * cos_p
        return x2, y2, z2

    normals = {
        'up': (0, 1, 0),
        'down': (0, -1, 0),
        'north': (0, 0, -1),
        'south': (0, 0, 1),
        'west': (-1, 0, 0),
        'east': (1, 0, 0),
    }

    triangles = []
    all_pts_cam = []

    for elem in elements:
        x0, y0, z0 = elem['from']
        x1, y1, z1 = elem['to']
        efaces = elem.get('faces', {})

        spec = [
            ('up', [(x0, y1, z0), (x1, y1, z0), (x1, y1, z1), (x0, y1, z1)], 1.0, [x0, z0, x1, z1]),
            ('down', [(x0, y0, z1), (x1, y0, z1), (x1, y0, z0), (x0, y0, z0)], 0.5, [x0, z0, x1, z1]),
            ('north', [(x1, y1, z0), (x0, y1, z0), (x0, y0, z0), (x1, y0, z0)], 0.82, [x0, 16 - y1, x1, 16 - y0]),
            ('south', [(x0, y1, z1), (x1, y1, z1), (x1, y0, z1), (x0, y0, z1)], 0.82, [x0, 16 - y1, x1, 16 - y0]),
            ('west', [(x0, y1, z0), (x0, y1, z1), (x0, y0, z1), (x0, y0, z0)], 0.65, [z0, 16 - y1, z1, 16 - y0]),
            ('east', [(x1, y1, z1), (x1, y1, z0), (x1, y0, z0), (x1, y0, z1)], 0.65, [z0, 16 - y1, z1, 16 - y0]),
        ]

        for fname, pts, light, def_uv in spec:
            if fname not in efaces:
                continue

            nx, ny, nz = normals[fname]
            _, _, cz = norm_to_cam(nx, ny, nz)
            if cz <= 1e-4:
                continue

            fdata = efaces[fname]
            tex_ref = fdata.get('texture', '')
            tex_arr = get_texture_arr(tex_ref)

            uv_box = fdata.get('uv', def_uv)
            u0, v0, u1, v1 = uv_box
            uvs = [(u0, v0), (u1, v0), (u1, v1), (u0, v1)]

            pts_cam = [world_to_cam(*p) for p in pts]

            for pc in pts_cam:
                all_pts_cam.append(pc)

            triangles.append({
                'pts': [pts_cam[0], pts_cam[1], pts_cam[2]],
                'uv': [uvs[0], uvs[1], uvs[2]],
                'tex_arr': tex_arr,
                'light': light
            })
            triangles.append({
                'pts': [pts_cam[0], pts_cam[2], pts_cam[3]],
                'uv': [uvs[0], uvs[2], uvs[3]],
                'tex_arr': tex_arr,
                'light': light
            })

    if not all_pts_cam:
        return

    cube_std_pts = [world_to_cam(x, y, z) for x in [0, 16] for y in [0, 16] for z in [0, 16]]
    cxs = [p[0] for p in cube_std_pts]
    cys = [p[1] for p in cube_std_pts]
    min_x, max_x = min(cxs), max(cxs)
    min_y, max_y = min(cys), max(cys)

    span_x = max_x - min_x
    span_y = max_y - min_y

    scale = min((size * 0.74) / span_x, (size * 0.74) / span_y)
    canvas_cx = size / 2.0
    canvas_cy = size / 2.0
    center_cam_x = (min_x + max_x) / 2.0
    center_cam_y = (min_y + max_y) / 2.0

    canvas = np.zeros((size, size, 4), dtype=np.uint8)
    z_buf = np.full((size, size), -1e9, dtype=np.float32)

    for tri in triangles:
        p0, p1, p2 = tri['pts']
        u0, u1, u2 = tri['uv']
        light = tri['light']
        tex_arr = tri['tex_arr']
        th, tw, _ = tex_arr.shape

        sx0 = (p0[0] - center_cam_x) * scale + canvas_cx
        sy0 = canvas_cy - (p0[1] - center_cam_y) * scale
        z0 = p0[2]

        sx1 = (p1[0] - center_cam_x) * scale + canvas_cx
        sy1 = canvas_cy - (p1[1] - center_cam_y) * scale
        z1 = p1[2]

        sx2 = (p2[0] - center_cam_x) * scale + canvas_cx
        sy2 = canvas_cy - (p2[1] - center_cam_y) * scale
        z2 = p2[2]

        min_px = max(0, int(math.floor(min(sx0, sx1, sx2))))
        max_px = min(size - 1, int(math.ceil(max(sx0, sx1, sx2))))
        min_py = max(0, int(math.floor(min(sy0, sy1, sy2))))
        max_py = min(size - 1, int(math.ceil(max(sy0, sy1, sy2))))

        denom = (sy1 - sy2) * (sx0 - sx2) + (sx2 - sx1) * (sy0 - sy2)
        if abs(denom) < 1e-6:
            continue

        for py in range(min_py, max_py + 1):
            for px in range(min_px, max_px + 1):
                w0 = ((sy1 - sy2) * (px - sx2) + (sx2 - sx1) * (py - sy2)) / denom
                w1 = ((sy2 - sy0) * (px - sx2) + (sx0 - sx2) * (py - sy2)) / denom
                w2 = 1.0 - w0 - w1
                if w0 >= -0.001 and w1 >= -0.001 and w2 >= -0.001:
                    depth = w0 * z0 + w1 * z1 + w2 * z2
                    if depth > z_buf[py, px]:
                        tu = w0 * u0[0] + w1 * u1[0] + w2 * u2[0]
                        tv = w0 * u0[1] + w1 * u1[1] + w2 * u2[1]
                        tx = int(math.floor(tu * (tw / 16.0))) % tw
                        ty = int(math.floor(tv * (th / 16.0))) % th
                        rgba = tex_arr[ty, tx]
                        if rgba[3] > 20:
                            r = int(min(255, max(0, rgba[0] * light)))
                            g = int(min(255, max(0, rgba[1] * light)))
                            b = int(min(255, max(0, rgba[2] * light)))
                            canvas[py, px] = [r, g, b, rgba[3]]
                            z_buf[py, px] = depth

    os.makedirs(os.path.dirname(os.path.abspath(out_path)), exist_ok=True)
    out_img = Image.fromarray(canvas, mode='RGBA')
    out_img.save(out_path)
    print(f"Rendered block: {out_path}")

RUST_GALLERY = [
    ("Placa militar", ["military_armor_plate", "weathered_military_plate", "rusted_military_plate"]),
    ("Faixas de perigo", ["hazard_stripe_block", "weathered_hazard_stripe", "corroded_hazard_stripe"]),
    ("Duto reforçado", ["reinforced_vent_duct", "rusted_vent_duct", "damaged_vent_duct"]),
    ("Grelha de aço", ["steel_vent_grate", "rusted_steel_vent_grate", "broken_steel_vent_grate"]),
]


def build_block_markdown_grid(columns=4):
    lines = []
    lines.append("<!-- BLOCK_GALLERY_START -->")
    lines.append("## 🧱 Novos Blocos, Maquinários & Automação")
    lines.append("")
    lines.append("<details>")
    lines.append(f"<summary><b>Clique para expandir o catálogo de maquinários e blocos ({len(BLOCKS)} modelos)</b></summary>")
    lines.append("<br>")
    lines.append("")
    lines.append('<table width="100%">')

    col_pct = f"{100 // columns}%"
    for i in range(0, len(BLOCKS), columns):
        row_blocks = BLOCKS[i:i + columns]
        lines.append("  <tr>")
        for b in row_blocks:
            rel_img_path = f"docs/images/blocks/{b['id']}.png"
            lines.append(f'    <td align="center" width="{col_pct}" valign="top">')
            lines.append(f'      <img src="{rel_img_path}" width="140" alt="{b["name"]}"><br>')
            lines.append(f'      <b>{b["name"]}</b><br>')
            lines.append(f'      <sub>{b["role"]}</sub>')
            lines.append('    </td>')
        if len(row_blocks) < columns:
            for _ in range(columns - len(row_blocks)):
                lines.append(f'    <td width="{col_pct}"></td>')
        lines.append("  </tr>")

    lines.append("</table>")
    lines.append("")
    lines.append("### Desgaste progressivo")
    lines.append("")
    lines.append("Cada família usa um bloco com três estados de ferrugem.")
    lines.append("")
    lines.append("| Bloco | Novo | Desgastado | Enferrujado |")
    lines.append("|:---|:---:|:---:|:---:|")
    for name, stages in RUST_GALLERY:
        images = [f'<img src="docs/images/blocks/{stage}.png" width="100" alt="{name}: estágio {index}">'
                  for index, stage in enumerate(stages)]
        lines.append(f"| {name} | " + " | ".join(images) + " |")
    lines.append("")
    lines.append('A placa militar no estágio final também pode apresentar uma aparência fraturada:')
    lines.append('')
    lines.append('<img src="docs/images/blocks/fractured_military_plate.png" width="100" alt="Placa militar fraturada">')
    lines.append("")
    lines.append("</details>")
    lines.append("<!-- BLOCK_GALLERY_END -->")
    return "\n".join(lines)

def update_readme_block_gallery():
    readme_path = "README.md"
    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    block_section = build_block_markdown_grid(columns=4)
    start_tag = "<!-- BLOCK_GALLERY_START -->"
    end_tag = "<!-- BLOCK_GALLERY_END -->"

    if start_tag in content and end_tag in content:
        pattern = re.compile(rf"{re.escape(start_tag)}.*?{re.escape(end_tag)}", re.DOTALL)
        new_content = pattern.sub(block_section, content)
    else:
        # Insert after ENTITY_GALLERY_END or before Como Jogar
        entity_end_tag = "<!-- ENTITY_GALLERY_END -->"
        if entity_end_tag in content:
            new_content = content.replace(entity_end_tag, f"{entity_end_tag}\n\n---\n\n{block_section}")
        else:
            anchor = "## 📖 Como Jogar (Wiki)"
            if anchor in content:
                new_content = content.replace(anchor, f"{block_section}\n\n---\n\n{anchor}")
            else:
                new_content = content + f"\n\n---\n\n{block_section}\n"

    with open(readme_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("README.md updated with block gallery!")

def main():
    parser = argparse.ArgumentParser(description="Render block isometric gallery and update README.md")
    parser.add_argument("--force", action="store_true", help="Force re-rendering of all images")
    args = parser.parse_args()

    model_dir = "src/main/resources/assets/sandstorm/models/block"
    out_dir = "docs/images/blocks"
    os.makedirs(out_dir, exist_ok=True)

    print(f"Processing {len(BLOCKS)} blocks...")
    for b in BLOCKS:
        out_path = os.path.join(out_dir, f"{b['id']}.png")
        if not args.force and os.path.exists(out_path):
            print(f"Skipping {b['id']} (already rendered)")
            continue

        mf = os.path.join(model_dir, b["model"])
        if not os.path.exists(mf):
            print(f"ERROR: Model file not found: {mf}")
            continue

        render_block_isometric(mf, out_path, size=300)

    for _, stages in RUST_GALLERY:
        for stage in stages:
            out_path = os.path.join(out_dir, f"{stage}.png")
            if args.force or not os.path.exists(out_path):
                render_block_isometric(os.path.join(model_dir, f"{stage}.json"), out_path, size=300)

    fractured = os.path.join(out_dir, "fractured_military_plate.png")
    if args.force or not os.path.exists(fractured):
        render_block_isometric(os.path.join(model_dir, "fractured_military_plate.json"), fractured, size=300)

    update_readme_block_gallery()

if __name__ == "__main__":
    main()

