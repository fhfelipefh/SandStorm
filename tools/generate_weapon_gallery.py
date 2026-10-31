import argparse
import json
import math
import os
import re
import sys
import numpy as np
from PIL import Image

WEAPONS = [
    {
        "id": "sonic_cannon",
        "name": "Canhão Sônico",
        "role": "Arma Acústica Pesada Anti-Verme",
        "type": "3d",
        "model": "src/main/resources/assets/sandstorm/models/item/sonic_cannon.json"
    },
    {
        "id": "plasma_rifle",
        "name": "Rifle de Plasma",
        "role": "Fuzil de Precisão Térmico",
        "type": "3d",
        "model": "src/main/resources/assets/sandstorm/models/item/plasma_rifle.json"
    },
    {
        "id": "vibro_crysknife",
        "name": "Faca Vibro-Crys",
        "role": "Lâmina Crio-Sônica de Dente de Verme",
        "type": "3d",
        "model": "src/main/resources/assets/sandstorm/models/item/vibro_crysknife.json"
    },
    {
        "id": "emp_blaster",
        "name": "Disruptor EMP",
        "role": "Desativação de Circuitos Hostis",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/emp_blaster.png"
    },
    {
        "id": "heavy_plasma_cannon",
        "name": "Canhão Pesado de Plasma",
        "role": "Artilharia Térmica de Demolição",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/heavy_plasma_cannon.png"
    },
    {
        "id": "kinetic_slug",
        "name": "Projétil Cinético Pesado",
        "role": "Munição Perfurante de Alta Densidade",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/kinetic_slug.png"
    },
    {
        "id": "stun_grenade",
        "name": "Granada de Choque",
        "role": "Sobrecarga Bioelétrica em Área",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/stun_grenade.png"
    },
    {
        "id": "smoke_grenade",
        "name": "Granada de Fumaça",
        "role": "Ocultamento Térmico e Visual",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/smoke_grenade.png"
    },
    {
        "id": "nutrient_bomb",
        "name": "Bomba de Nutrientes",
        "role": "Terraformação & Impulso Biológico",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/nutrient_bomb.png"
    },
    {
        "id": "tactical_detonator",
        "name": "Detonador Tático",
        "role": "Controle Remoto de Detonação",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/tactical_detonator.png"
    },
    {
        "id": "space_suit_helmet",
        "name": "Capacete Espacial",
        "role": "Suporte Vital & HUD de Varredura",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/space_suit_helmet.png"
    },
    {
        "id": "space_suit_chestplate",
        "name": "Traje Espacial (Torso)",
        "role": "Blindagem de Vácuo & Propulsão",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/space_suit_chestplate.png"
    },
    {
        "id": "anomaly_radar",
        "name": "Radar de Anomalias",
        "role": "Detecção de Radiação & Falhas",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/anomaly_radar.png"
    },
    {
        "id": "reprogrammer_tool",
        "name": "Ferramenta Reprogramadora",
        "role": "Recodificação e Hack de Robôs",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/reprogrammer_tool.png"
    },
    {
        "id": "cybernetic_command_uplink",
        "name": "Uplink Cibernético",
        "role": "Comando Remoto de Drones & Ciborgues",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/cybernetic_command_uplink.png"
    },
    {
        "id": "sandboard",
        "name": "Prancha de Areia (Sandboard)",
        "role": "Locomoção Tática de Alta Velocidade",
        "type": "2d",
        "texture": "src/main/resources/assets/sandstorm/textures/item/sandboard.png"
    }
]

def resolve_item_texture(tex_key, textures_dict):
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
            f"src/main/resources/assets/sandstorm/textures/item/{os.path.basename(rel)}.png"
        ]
        for c in candidates:
            if os.path.exists(c):
                return c
    return None

def render_item_3d(model_json_path, out_path, size=300):
    with open(model_json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    elements = data.get('elements', [])
    textures = data.get('textures', {})

    loaded_textures = {}
    def get_texture_arr(tex_ref):
        tf = resolve_item_texture(tex_ref, textures)
        if not tf or not os.path.exists(tf):
            arr = np.full((32, 32, 4), 180, dtype=np.uint8)
            arr[:, :, 3] = 255
            return arr
        if tf not in loaded_textures:
            img = Image.open(tf).convert('RGBA')
            loaded_textures[tf] = np.array(img)
        return loaded_textures[tf]

    # Isometric presentation for weapons
    yaw = math.radians(-45)
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

    triangles = []
    all_pts_cam = []

    for elem in elements:
        x0, y0, z0 = elem['from']
        x1, y1, z1 = elem['to']
        efaces = elem.get('faces', {})

        spec = [
            ('up', [(x0, y1, z0), (x1, y1, z0), (x1, y1, z1), (x0, y1, z1)], 1.0),
            ('down', [(x0, y0, z1), (x1, y0, z1), (x1, y0, z0), (x0, y0, z0)], 0.5),
            ('north', [(x1, y1, z0), (x0, y1, z0), (x0, y0, z0), (x1, y0, z0)], 0.82),
            ('south', [(x0, y1, z1), (x1, y1, z1), (x1, y0, z1), (x0, y0, z1)], 0.82),
            ('west', [(x0, y1, z0), (x0, y1, z1), (x0, y0, z1), (x0, y0, z0)], 0.65),
            ('east', [(x1, y1, z1), (x1, y1, z0), (x1, y0, z0), (x1, y0, z1)], 0.65),
        ]

        for fname, pts, light in spec:
            if fname not in efaces:
                continue
            fdata = efaces[fname]
            tex_ref = fdata.get('texture', '')
            tex_arr = get_texture_arr(tex_ref)
            th, tw, _ = tex_arr.shape

            uv_box = fdata.get('uv', [0, 0, 16, 16])
            u0, v0, u1, v1 = uv_box
            uvs = [(u0, v0), (u1, v0), (u1, v1), (u0, v1)]

            pts_cam = [world_to_cam(*p) for p in pts]

            v01 = (pts_cam[1][0] - pts_cam[0][0], pts_cam[1][1] - pts_cam[0][1])
            v02 = (pts_cam[2][0] - pts_cam[0][0], pts_cam[2][1] - pts_cam[0][1])
            cross_z = v01[0] * v02[1] - v01[1] * v02[0]
            if cross_z <= 1e-4:
                continue

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

    xs = [p[0] for p in all_pts_cam]
    ys = [p[1] for p in all_pts_cam]
    min_x, max_x = min(xs), max(xs)
    min_y, max_y = min(ys), max(ys)

    span_x = max(max_x - min_x, 1.0)
    span_y = max(max_y - min_y, 1.0)

    scale = min((size * 0.76) / span_x, (size * 0.76) / span_y)
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
                        tx = int(math.floor(tu)) % tw
                        ty = int(math.floor(tv)) % th
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
    print(f"Rendered 3D weapon: {out_path}")

def render_item_2d(tex_path, out_path, size=300):
    img = Image.open(tex_path).convert('RGBA')
    w, h = img.size

    target_dim = int(size * 0.72)
    scale_factor = max(1, target_dim // max(w, h))
    new_w = w * scale_factor
    new_h = h * scale_factor

    resized = img.resize((new_w, new_h), Image.NEAREST)

    canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    paste_x = (size - new_w) // 2
    paste_y = (size - new_h) // 2

    canvas.paste(resized, (paste_x, paste_y), mask=resized)
    os.makedirs(os.path.dirname(os.path.abspath(out_path)), exist_ok=True)
    canvas.save(out_path)
    print(f"Rendered 2D weapon: {out_path}")

def build_weapon_markdown_grid(columns=4):
    lines = []
    lines.append("<!-- WEAPON_GALLERY_START -->")
    lines.append("## ⚔️ Arsenal Tático, Armas & Equipamentos")
    lines.append("")
    lines.append("<details>")
    lines.append(f"<summary><b>Clique para expandir o arsenal de armas e equipamentos ({len(WEAPONS)} modelos)</b></summary>")
    lines.append("<br>")
    lines.append("")
    lines.append('<table width="100%">')

    col_pct = f"{100 // columns}%"
    for i in range(0, len(WEAPONS), columns):
        row_weapons = WEAPONS[i:i + columns]
        lines.append("  <tr>")
        for w in row_weapons:
            rel_img_path = f"docs/images/weapons/{w['id']}.png"
            lines.append(f'    <td align="center" width="{col_pct}" valign="top">')
            lines.append(f'      <img src="{rel_img_path}" width="140" alt="{w["name"]}"><br>')
            lines.append(f'      <b>{w["name"]}</b><br>')
            lines.append(f'      <sub>{w["role"]}</sub>')
            lines.append('    </td>')
        if len(row_weapons) < columns:
            for _ in range(columns - len(row_weapons)):
                lines.append(f'    <td width="{col_pct}"></td>')
        lines.append("  </tr>")

    lines.append("</table>")
    lines.append("")
    lines.append("</details>")
    lines.append("<!-- WEAPON_GALLERY_END -->")
    return "\n".join(lines)

def update_readme_weapon_gallery():
    readme_path = "README.md"
    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    weapon_section = build_weapon_markdown_grid(columns=4)
    start_tag = "<!-- WEAPON_GALLERY_START -->"
    end_tag = "<!-- WEAPON_GALLERY_END -->"

    if start_tag in content and end_tag in content:
        pattern = re.compile(rf"{re.escape(start_tag)}.*?{re.escape(end_tag)}", re.DOTALL)
        new_content = pattern.sub(weapon_section, content)
    else:
        # Insert after BLOCK_GALLERY_END, or after ENTITY_GALLERY_END, or before Como Jogar
        block_end_tag = "<!-- BLOCK_GALLERY_END -->"
        entity_end_tag = "<!-- ENTITY_GALLERY_END -->"
        if block_end_tag in content:
            new_content = content.replace(block_end_tag, f"{block_end_tag}\n\n---\n\n{weapon_section}")
        elif entity_end_tag in content:
            new_content = content.replace(entity_end_tag, f"{entity_end_tag}\n\n---\n\n{weapon_section}")
        else:
            anchor = "## 📖 Como Jogar (Wiki)"
            if anchor in content:
                new_content = content.replace(anchor, f"{weapon_section}\n\n---\n\n{anchor}")
            else:
                new_content = content + f"\n\n---\n\n{weapon_section}\n"

    with open(readme_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("README.md updated with weapon gallery!")

def main():
    parser = argparse.ArgumentParser(description="Render weapon gallery and update README.md")
    parser.add_argument("--force", action="store_true", help="Force re-rendering of all images")
    args = parser.parse_args()

    out_dir = "docs/images/weapons"
    os.makedirs(out_dir, exist_ok=True)

    print(f"Processing {len(WEAPONS)} weapons and equipment...")
    for w in WEAPONS:
        out_path = os.path.join(out_dir, f"{w['id']}.png")
        if not args.force and os.path.exists(out_path):
            print(f"Skipping {w['id']} (already rendered)")
            continue

        if w["type"] == "3d":
            if not os.path.exists(w["model"]):
                print(f"ERROR: Model file not found: {w['model']}")
                continue
            render_item_3d(w["model"], out_path, size=300)
        else:
            if not os.path.exists(w["texture"]):
                print(f"ERROR: Texture file not found: {w['texture']}")
                continue
            render_item_2d(w["texture"], out_path, size=300)

    update_readme_weapon_gallery()

if __name__ == "__main__":
    main()

