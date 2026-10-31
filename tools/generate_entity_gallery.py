import argparse
import math
import os
import re
import sys
import numpy as np
from PIL import Image

ENTITIES = [
    {
        "id": "megazord",
        "name": "Titã Megazord",
        "role": "Veículo Mecatrônico Pesado",
        "model": "MegazordModel.java",
        "texture": "textures/entity/megazord/megazord.png"
    },
    {
        "id": "warden_android",
        "name": "Androide Guardião",
        "role": "Androide Alfa Gravimétrico & Acústico",
        "model": "WardenAndroidModel.java",
        "texture": "textures/entity/warden_android/warden_android.png"
    },
    {
        "id": "fanghorn",
        "name": "Presalonga (Fanghorn)",
        "role": "Escolta Térmica & Combate",
        "model": "FanghornModel.java",
        "texture": "textures/entity/fanghorn/fanghorn.png"
    },
    {
        "id": "frostclaw",
        "name": "Garragélida (Frostclaw)",
        "role": "Predador Criogênico Articular",
        "model": "FrostclawModel.java",
        "texture": "textures/entity/frostclaw/frostclaw.png"
    },
    {
        "id": "glinthawk",
        "name": "Falcão Cintilante (Glinthawk)",
        "role": "Drone Predador Aéreo",
        "model": "GlinthawkModel.java",
        "texture": "textures/entity/glinthawk/glinthawk.png"
    },
    {
        "id": "grazer",
        "name": "Pastador (Grazer)",
        "role": "Coleta Térmica & Terraformação",
        "model": "GrazerModel.java",
        "texture": "textures/entity/grazer/grazer.png"
    },
    {
        "id": "waterwing",
        "name": "Asa d'Água (Waterwing)",
        "role": "Anfíbio Aquático & Aéreo",
        "model": "WaterwingModel.java",
        "texture": "textures/entity/waterwing/waterwing.png"
    },
    {
        "id": "cyber_steed",
        "name": "Corcel Cibernético",
        "role": "Montaria Rápida & Bateria Móvel",
        "model": "CyberSteedModel.java",
        "texture": "textures/entity/cyber_steed/cyber_steed_wild.png"
    },
    {
        "id": "cyber_hound",
        "name": "Cão Cibernético",
        "role": "Guardião Autônomo & Rastreador",
        "model": "CyberHoundModel.java",
        "texture": "textures/entity/cyber_hound/cyber_hound_wild.png"
    },
    {
        "id": "cybernetic_golem",
        "name": "Golem Cibernético",
        "role": "Defesa Pesada de Instalações",
        "model": "CyberneticGolemModel.java",
        "texture": "textures/entity/cybernetic_golem/cybernetic_golem_iron.png"
    },
    {
        "id": "sandworm",
        "name": "Verme da Areia (Sandworm)",
        "role": "Superpredador Sísmico Titânico",
        "model": "SandwormModel.java",
        "texture": "textures/entity/sandworm/sandworm.png"
    },
    {
        "id": "scrap_sentinel",
        "name": "Sentinela de Sucata",
        "role": "Patrulheiro Autônomo de Ruínas",
        "model": "ScrapSentinelModel.java",
        "texture": "textures/entity/scrap_sentinel/scrap_sentinel_active.png"
    },
    {
        "id": "defective_sentinel",
        "name": "Sentinela Defeituosa",
        "role": "Unidade Hostil Corrompida",
        "model": "DefectiveSentinelModel.java",
        "texture": "textures/entity/defective_sentinel/defective_sentinel.png"
    },
    {
        "id": "derelict_automaton",
        "name": "Autômato Abandonado",
        "role": "Unidade Decrépita Reciclável",
        "model": "DerelictAutomatonModel.java",
        "texture": "textures/entity/derelict_automaton/derelict_automaton.png"
    },
    {
        "id": "laborer_unit",
        "name": "Unidade Operária",
        "role": "Androide Auxiliar de Mineração",
        "model": "LaborerUnitModel.java",
        "texture": "textures/entity/laborer_unit/laborer_unit.png"
    },
    {
        "id": "nomad_scavenger",
        "name": "Sucateiro Nômade",
        "role": "Comerciante do Deserto Profundo",
        "model": "NomadScavengerModel.java",
        "texture": "textures/entity/nomad_scavenger/nomad_scavenger.png"
    },
    {
        "id": "aquifer_beetle",
        "name": "Besouro Aquífero",
        "role": "Bio-Reservatório de Água",
        "model": "AquiferBeetleModel.java",
        "texture": "textures/entity/aquifer_beetle/aquifer_beetle.png"
    },
    {
        "id": "cyborg_excavator",
        "name": "Ciborgue Escavador",
        "role": "Especialista em Perfuração",
        "model": "CyborgModel.java",
        "texture": "textures/entity/cyborg/cyborg_excavator.png"
    },
    {
        "id": "cyborg_builder",
        "name": "Ciborgue Construtor",
        "role": "Especialista em Montagem",
        "model": "CyborgModel.java",
        "texture": "textures/entity/cyborg/cyborg_builder.png"
    },
    {
        "id": "cyborg_harvester",
        "name": "Ciborgue Colhedor",
        "role": "Especialista em Agricultura",
        "model": "CyborgModel.java",
        "texture": "textures/entity/cyborg/cyborg_harvester.png"
    },
    {
        "id": "scout_drone",
        "name": "Drone Explorador",
        "role": "Reconhecimento & Vigilância Aérea",
        "model": "ScoutDroneModel.java",
        "texture": "textures/entity/scout_drone/scout_drone.png"
    },
    {
        "id": "crawler_drone",
        "name": "Drone Rastejador",
        "role": "Inspeção & Manutenção Interna",
        "model": "CrawlerDroneModel.java",
        "texture": "textures/entity/crawler_drone/crawler_drone.png"
    },
    {
        "id": "cargo_drone",
        "name": "Drone de Carga",
        "role": "Logística Autônoma de Itens",
        "model": "CargoDroneModel.java",
        "texture": "textures/entity/cargo_drone/cargo_drone.png"
    },
    {
        "id": "builder_drone",
        "name": "Drone Construtor",
        "role": "Fabricação Remota de Estruturas",
        "model": "BuilderDroneModel.java",
        "texture": "textures/entity/builder_drone/builder_drone.png"
    },
    {
        "id": "excavator_vehicle",
        "name": "Veículo Escavador",
        "role": "Maquinário Pesado Tripulado",
        "model": "ExcavatorVehicleModel.java",
        "texture": "textures/entity/excavator_vehicle/excavator_vehicle.png"
    },
    {
        "id": "autonomous_sonic_turret",
        "name": "Torreta Sônica Autônoma",
        "role": "Defesa Perimetral Anti-Verme",
        "model": "AutonomousSonicTurretModel.java",
        "texture": "textures/entity/autonomous_sonic_turret/autonomous_sonic_turret_mechanisms.png"
    },
    {
        "id": "sandboard",
        "name": "Prancha de Areia (Sandboard)",
        "role": "Locomoção Veloz nas Dunas",
        "model": "SandboardModel.java",
        "texture": "textures/entity/sandboard/sandboard.png"
    }
]

def rot_x(rad):
    c, s = math.cos(rad), math.sin(rad)
    return np.array([
        [1, 0, 0, 0],
        [0, c, -s, 0],
        [0, s, c, 0],
        [0, 0, 0, 1]
    ], dtype=np.float32)

def rot_y(rad):
    c, s = math.cos(rad), math.sin(rad)
    return np.array([
        [c, 0, s, 0],
        [0, 1, 0, 0],
        [-s, 0, c, 0],
        [0, 0, 0, 1]
    ], dtype=np.float32)

def rot_z(rad):
    c, s = math.cos(rad), math.sin(rad)
    return np.array([
        [c, -s, 0, 0],
        [s, c, 0, 0],
        [0, 0, 1, 0],
        [0, 0, 0, 1]
    ], dtype=np.float32)

def translate(tx, ty, tz):
    return np.array([
        [1, 0, 0, tx],
        [0, 1, 0, ty],
        [0, 0, 1, tz],
        [0, 0, 0, 1]
    ], dtype=np.float32)

def eval_java_expr(expr):
    expr = re.sub(r'\(float\)', '', expr)
    expr = re.sub(r'f\b', '', expr)
    expr = expr.replace('Math.PI', str(math.pi))
    expr = expr.strip()
    try:
        return float(eval(expr, {"__builtins__": {}}, {"math": math}))
    except Exception:
        return 0.0

def parse_model_hierarchy(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    content_clean = re.sub(r'//.*', '', content)

    # Pre-scan variable assignments to child part names (e.g. PartDefinition seg1 = root.addOrReplaceChild("segment_1", ...))
    var_to_name = {'root': 'root'}
    for m in re.finditer(r'(?:PartDefinition\s+)?(\w+)\s*=\s*(\w+)\.addOrReplaceChild\s*\(\s*["\']([^"\']+)["\']', content_clean):
        var_to_name[m.group(1)] = m.group(3)

    is_sandworm = 'SandwormModel' in file_path
    part_nodes = {}

    pattern = re.compile(
        r'(\w+)\.addOrReplaceChild\s*\(\s*["\']([^"\']+)["\']\s*,\s*CubeListBuilder\.create\(\)(.*?)(PartPose\.ZERO|PartPose\.offsetAndRotation\s*\(.*?\)\s*\)|PartPose\.offset\s*\(.*?\))',
        re.DOTALL
    )

    for m in pattern.finditer(content_clean):
        parent_var = m.group(1)
        name = m.group(2)
        chunk = m.group(3)
        pose_str = m.group(4)

        ox, oy, oz = 0.0, 0.0, 0.0
        rx, ry, rz = 0.0, 0.0, 0.0

        if 'offsetAndRotation' in pose_str:
            args_m = re.search(r'offsetAndRotation\s*\((.*)\)', pose_str, re.DOTALL)
            if args_m:
                raw_args = args_m.group(1).replace('\n', ' ')
                parts = []
                cur = ''
                depth = 0
                for ch in raw_args:
                    if ch == '(': depth += 1
                    elif ch == ')': depth -= 1
                    elif ch == ',' and depth == 0:
                        parts.append(cur.strip())
                        cur = ''
                        continue
                    cur += ch
                if cur.strip():
                    parts.append(cur.strip())
                if len(parts) >= 3:
                    ox = eval_java_expr(parts[0])
                    oy = eval_java_expr(parts[1])
                    oz = eval_java_expr(parts[2])
                if len(parts) >= 6:
                    rx = eval_java_expr(parts[3])
                    ry = eval_java_expr(parts[4])
                    rz = eval_java_expr(parts[5])
        elif 'offset' in pose_str:
            args_m = re.search(r'offset\s*\(\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*\)', pose_str)
            if args_m:
                ox = float(args_m.group(1))
                oy = float(args_m.group(2))
                oz = float(args_m.group(3))

        # Dynamic breach pose for Sandworm so it emerges majestically from the sand
        if is_sandworm:
            if name.startswith('sub_ring'):
                continue
            if name.startswith('segment_'):
                seg_idx = int(name.split('_')[1])
                rx = 0.14 * (1.1 - seg_idx * 0.04)
                ry = -0.08 * (1.0 + seg_idx * 0.05)
                rz = 0.03 * seg_idx
            elif name == 'neck':
                rx = 0.14 * 0.6
                ry = -0.08 * 1.5
                rz = 0.03 * 10
            elif name == 'head':
                rx = 0.14 * 0.4
                ry = -0.08 * 1.8
                rz = 0.0

        boxes = []
        box_pattern = re.compile(
            r'texOffs\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)\s*\.addBox\s*\(\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*\)'
        )
        for bm in box_pattern.finditer(chunk):
            u, v = int(bm.group(1)), int(bm.group(2))
            x, y, z = float(bm.group(3)), float(bm.group(4)), float(bm.group(5))
            dx, dy, dz = float(bm.group(6)), float(bm.group(7)), float(bm.group(8))
            boxes.append({'u': u, 'v': v, 'x': x, 'y': y, 'z': z, 'dx': dx, 'dy': dy, 'dz': dz})

        actual_parent = var_to_name.get(parent_var, parent_var)
        part_nodes[name] = {
            'parent_name': actual_parent if actual_parent != 'root' else None,
            'ox': ox, 'oy': oy, 'oz': oz,
            'rx': rx, 'ry': ry, 'rz': rz,
            'boxes': boxes
        }

    local_matrices = {}
    for name, node in part_nodes.items():
        T = translate(node['ox'], node['oy'], node['oz'])
        R = rot_z(node['rz']) @ rot_y(node['ry']) @ rot_x(node['rx'])
        local_matrices[name] = T @ R

    world_matrices = {}
    def get_world_matrix(name):
        if name in world_matrices:
            return world_matrices[name]
        p_name = part_nodes[name]['parent_name']
        if p_name and p_name in part_nodes:
            p_mat = get_world_matrix(p_name)
        else:
            p_mat = np.eye(4, dtype=np.float32)
        w_mat = p_mat @ local_matrices[name]
        world_matrices[name] = w_mat
        return w_mat

    all_boxes = []
    for name, node in part_nodes.items():
        w_mat = get_world_matrix(name)
        for b in node['boxes']:
            all_boxes.append({
                'part': name,
                'matrix': w_mat,
                'u': b['u'], 'v': b['v'],
                'x': b['x'], 'y': b['y'], 'z': b['z'],
                'dx': b['dx'], 'dy': b['dy'], 'dz': b['dz']
            })

    return all_boxes

def render_isometric(boxes, texture_path, output_path, img_size=(400, 400), yaw_deg=-135, pitch_deg=30):
    tex_img = Image.open(texture_path).convert('RGBA')
    tex_arr = np.array(tex_img)
    tex_h, tex_w, _ = tex_arr.shape

    yaw = math.radians(yaw_deg)
    pitch = math.radians(pitch_deg)

    cos_y, sin_y = math.cos(yaw), math.sin(yaw)
    cos_p, sin_p = math.cos(pitch), math.sin(pitch)

    def world_to_cam(x, y, z):
        my_y = -y
        x1 = x * cos_y + z * sin_y
        y1 = my_y
        z1 = -x * sin_y + z * cos_y
        x2 = x1
        y2 = y1 * cos_p - z1 * sin_p
        z2 = y1 * sin_p + z1 * cos_p
        return x2, y2, z2

    triangles = []
    all_pts_cam = []

    for box in boxes:
        w_mat = box['matrix']
        x0, y0, z0 = box['x'], box['y'], box['z']
        dx, dy, dz = box['dx'], box['dy'], box['dz']
        x1, y1, z1 = x0 + dx, y0 + dy, z0 + dz
        u, v = box['u'], box['v']

        u0 = u
        u1 = u + dz
        u2 = u + dz + dx
        u3 = u + 2 * dz + dx
        u4 = u + 2 * (dz + dx)

        v0 = v
        v1 = v + dz
        v2 = v + dz + dy

        faces = [
            # Top face (Y = y0, normal (0, -1, 0))
            {
                'pts': [(x1, y0, z0), (x0, y0, z0), (x0, y0, z1), (x1, y0, z1)],
                'uv': [(u2, v1), (u1, v1), (u1, v0), (u2, v0)],
                'norm': (0, -1, 0)
            },
            # Bottom face (Y = y1, normal (0, 1, 0))
            {
                'pts': [(x1, y1, z1), (x0, y1, z1), (x0, y1, z0), (x1, y1, z0)],
                'uv': [(u2, v0), (u3, v0), (u3, v1), (u2, v1)],
                'norm': (0, 1, 0)
            },
            # North / Front face (Z = z0, normal (0, 0, -1))
            {
                'pts': [(x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0)],
                'uv': [(u1, v1), (u2, v1), (u2, v2), (u1, v2)],
                'norm': (0, 0, -1)
            },
            # South / Back face (Z = z1, normal (0, 0, 1))
            {
                'pts': [(x1, y0, z1), (x0, y0, z1), (x0, y1, z1), (x1, y1, z1)],
                'uv': [(u3, v1), (u4, v1), (u4, v2), (u3, v2)],
                'norm': (0, 0, 1)
            },
            # West / Mob Right face (X = x0, normal (-1, 0, 0))
            {
                'pts': [(x0, y0, z1), (x0, y0, z0), (x0, y1, z0), (x0, y1, z1)],
                'uv': [(u0, v1), (u1, v1), (u1, v2), (u0, v2)],
                'norm': (-1, 0, 0)
            },
            # East / Mob Left face (X = x1, normal (1, 0, 0))
            {
                'pts': [(x1, y0, z0), (x1, y0, z1), (x1, y1, z1), (x1, y1, z0)],
                'uv': [(u2, v1), (u3, v1), (u3, v2), (u2, v2)],
                'norm': (1, 0, 0)
            }
        ]

        for face in faces:
            pts_world = []
            for p in face['pts']:
                v4 = np.array([p[0], p[1], p[2], 1.0], dtype=np.float32)
                pw4 = w_mat @ v4
                pts_world.append((pw4[0], pw4[1], pw4[2]))

            n4 = np.array([face['norm'][0], face['norm'][1], face['norm'][2], 0.0], dtype=np.float32)
            nw4 = w_mat @ n4
            nx, ny, nz = nw4[0], nw4[1], nw4[2]
            n_len = math.sqrt(nx*nx + ny*ny + nz*nz)
            if n_len > 1e-5:
                nx, ny, nz = nx/n_len, ny/n_len, nz/n_len

            if ny < -0.5:
                light = 1.0
            elif ny > 0.5:
                light = 0.52
            elif abs(nz) > abs(nx):
                light = 0.82
            else:
                light = 0.68

            pts_cam = [world_to_cam(*pw) for pw in pts_world]
            for pc in pts_cam:
                all_pts_cam.append(pc)

            triangles.append({
                'pts': [pts_cam[0], pts_cam[1], pts_cam[2]],
                'uv': [face['uv'][0], face['uv'][1], face['uv'][2]],
                'light': light
            })
            triangles.append({
                'pts': [pts_cam[0], pts_cam[2], pts_cam[3]],
                'uv': [face['uv'][0], face['uv'][2], face['uv'][3]],
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

    target_w, target_h = img_size
    scale = min((target_w * 0.74) / span_x, (target_h * 0.74) / span_y)

    center_cam_x = (min_x + max_x) / 2.0
    center_cam_y = (min_y + max_y) / 2.0

    canvas_cx = target_w / 2.0
    canvas_cy = target_h / 2.0

    canvas = np.zeros((target_h, target_w, 4), dtype=np.uint8)
    z_buffer = np.full((target_h, target_w), -1e9, dtype=np.float32)

    for tri in triangles:
        p0, p1, p2 = tri['pts']
        u0, u1, u2 = tri['uv']
        light = tri['light']

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
        max_px = min(target_w - 1, int(math.ceil(max(sx0, sx1, sx2))))
        min_py = max(0, int(math.floor(min(sy0, sy1, sy2))))
        max_py = min(target_h - 1, int(math.ceil(max(sy0, sy1, sy2))))

        denom = (sy1 - sy2) * (sx0 - sx2) + (sx2 - sx1) * (sy0 - sy2)
        if abs(denom) < 1e-6:
            continue

        for py in range(min_py, max_py + 1):
            for px in range(min_px, max_px + 1):
                w0 = ((sy1 - sy2) * (px - sx2) + (sx2 - sx1) * (py - sy2)) / denom
                w1 = ((sy2 - sy0) * (px - sx2) + (sx0 - sx2) * (py - sy2)) / denom
                w2 = 1.0 - w0 - w1

                if w0 >= -0.01 and w1 >= -0.01 and w2 >= -0.01:
                    depth = w0 * z0 + w1 * z1 + w2 * z2
                    if depth > z_buffer[py, px]:
                        tu = w0 * u0[0] + w1 * u1[0] + w2 * u2[0]
                        tv = w0 * u0[1] + w1 * u1[1] + w2 * u2[1]

                        tex_x = int(math.floor(tu)) % tex_w
                        tex_y = int(math.floor(tv)) % tex_h

                        rgba = tex_arr[tex_y, tex_x]
                        if rgba[3] > 20:
                            r = int(min(255, max(0, rgba[0] * light)))
                            g = int(min(255, max(0, rgba[1] * light)))
                            b = int(min(255, max(0, rgba[2] * light)))
                            canvas[py, px] = [r, g, b, rgba[3]]
                            z_buffer[py, px] = depth

    os.makedirs(os.path.dirname(os.path.abspath(output_path)), exist_ok=True)
    out_img = Image.fromarray(canvas, mode='RGBA')
    out_img.save(output_path)
    print(f"Rendered: {output_path}")

def build_markdown_grid(columns=4):
    lines = []
    lines.append("<!-- ENTITY_GALLERY_START -->")
    lines.append("## 👾 Entidades & Mobs Cibernéticos")
    lines.append("")
    lines.append(f"<details>")
    lines.append(f"<summary><b>Clique para expandir a galeria isométrica de entidades ({len(ENTITIES)} modelos)</b></summary>")
    lines.append("<br>")
    lines.append("")
    lines.append('<table width="100%">')

    col_pct = f"{100 // columns}%"
    for i in range(0, len(ENTITIES), columns):
        row_entities = ENTITIES[i:i + columns]
        lines.append("  <tr>")
        for ent in row_entities:
            rel_img_path = f"docs/images/entities/{ent['id']}.png"
            lines.append(f'    <td align="center" width="{col_pct}" valign="top">')
            lines.append(f'      <img src="{rel_img_path}" width="140" alt="{ent["name"]}"><br>')
            lines.append(f'      <b>{ent["name"]}</b><br>')
            lines.append(f'      <sub>{ent["role"]}</sub>')
            lines.append('    </td>')
        # Fill empty cells if row not full
        if len(row_entities) < columns:
            for _ in range(columns - len(row_entities)):
                lines.append(f'    <td width="{col_pct}"></td>')
        lines.append("  </tr>")

    lines.append("</table>")
    lines.append("")
    lines.append("</details>")
    lines.append("<!-- ENTITY_GALLERY_END -->")
    return "\n".join(lines)

def update_readme():
    readme_path = "README.md"
    with open(readme_path, "r", encoding="utf-8") as f:
        content = f.read()

    gallery_block = build_markdown_grid(columns=4)

    start_tag = "<!-- ENTITY_GALLERY_START -->"
    end_tag = "<!-- ENTITY_GALLERY_END -->"

    if start_tag in content and end_tag in content:
        pattern = re.compile(rf"{re.escape(start_tag)}.*?{re.escape(end_tag)}", re.DOTALL)
        new_content = pattern.sub(gallery_block, content)
    else:
        # Insert before '## 📖 Como Jogar (Wiki)' or near the end of Demonstração em Jogo
        anchor = "## 📖 Como Jogar (Wiki)"
        if anchor in content:
            new_content = content.replace(anchor, f"{gallery_block}\n\n---\n\n{anchor}")
        else:
            new_content = content + f"\n\n---\n\n{gallery_block}\n"

    with open(readme_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("README.md updated successfully with dynamic entity gallery!")

def main():
    parser = argparse.ArgumentParser(description="Render entity gallery images and update README.md")
    parser.add_argument("--force", action="store_true", help="Force re-rendering of all images")
    args = parser.parse_args()

    model_dir = "src/client/java/com/fhfelipefh/sandstorm/client/renderer"
    tex_dir = "src/main/resources/assets/sandstorm"
    out_dir = "docs/images/entities"
    os.makedirs(out_dir, exist_ok=True)

    print(f"Processing {len(ENTITIES)} entities...")
    for ent in ENTITIES:
        out_path = os.path.join(out_dir, f"{ent['id']}.png")
        if not args.force and os.path.exists(out_path):
            print(f"Skipping {ent['id']} (already rendered)")
            continue

        mf = os.path.join(model_dir, ent["model"])
        tf = os.path.join(tex_dir, ent["texture"])

        if not os.path.exists(mf):
            print(f"ERROR: Model file not found: {mf}")
            continue
        if not os.path.exists(tf):
            print(f"ERROR: Texture file not found: {tf}")
            continue

        boxes = parse_model_hierarchy(mf)
        render_isometric(boxes, tf, out_path)

    update_readme()

if __name__ == "__main__":
    main()

