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

ANIMATED_ENTITIES = {
    "builder_drone",
    "cargo_drone",
    "scout_drone",
    "crawler_drone",
    "glinthawk",
    "waterwing",
    "autonomous_sonic_turret",
    "warden_android",
    "sandworm",
    "megazord",
    "cyber_steed",
    "cyber_hound"
}

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

def apply_entity_pose(part_nodes, entity_id, t):
    if entity_id == 'builder_drone':
        if 'body' in part_nodes: part_nodes['body']['oy'] += math.sin(t) * 1.5
        spin = t * 2.0
        if 'rotor1' in part_nodes: part_nodes['rotor1']['ry'] = spin
        if 'rotor2' in part_nodes: part_nodes['rotor2']['ry'] = -spin
        if 'rotor3' in part_nodes: part_nodes['rotor3']['ry'] = -spin
        if 'rotor4' in part_nodes: part_nodes['rotor4']['ry'] = spin

    elif entity_id == 'cargo_drone':
        if 'body' in part_nodes: part_nodes['body']['oy'] += math.sin(t) * 1.5
        spin = t * 2.0
        if 'rotor1' in part_nodes: part_nodes['rotor1']['ry'] = spin
        if 'rotor2' in part_nodes: part_nodes['rotor2']['ry'] = -spin
        if 'rotor3' in part_nodes: part_nodes['rotor3']['ry'] = -spin
        if 'rotor4' in part_nodes: part_nodes['rotor4']['ry'] = spin
        if 'cargoClaw' in part_nodes:
            part_nodes['cargoClaw']['rz'] = math.sin(t) * 0.1
            part_nodes['cargoClaw']['rx'] = math.cos(t) * 0.08

    elif entity_id == 'scout_drone':
        if 'right_leg' in part_nodes: part_nodes['right_leg']['rx'] = math.cos(t) * 0.65
        if 'left_leg' in part_nodes: part_nodes['left_leg']['rx'] = math.cos(t + math.pi) * 0.65
        if 'left_arm' in part_nodes: part_nodes['left_arm']['rx'] = math.cos(t) * 0.45
        if 'right_arm' in part_nodes:
            part_nodes['right_arm']['rx'] = -math.pi / 2.0 + math.sin(t * 2.0) * 0.06
            part_nodes['right_arm']['ry'] = math.sin(t) * 0.15
        if 'head' in part_nodes:
            part_nodes['head']['ry'] = math.sin(t) * 0.35
            part_nodes['head']['rx'] = math.sin(t * 2.0) * 0.08

    elif entity_id == 'crawler_drone':
        legSpreadZ = math.pi / 4.0
        base_poses = {
            'right_hind_leg': (-legSpreadZ * 0.74, math.pi / 4.0),
            'left_hind_leg': (legSpreadZ * 0.74, -math.pi / 4.0),
            'right_middle_hind_leg': (-legSpreadZ * 0.58, math.pi / 8.0),
            'left_middle_hind_leg': (legSpreadZ * 0.58, -math.pi / 8.0),
            'right_middle_front_leg': (-legSpreadZ * 0.58, -math.pi / 8.0),
            'left_middle_front_leg': (legSpreadZ * 0.58, math.pi / 8.0),
            'right_front_leg': (-legSpreadZ * 0.74, -math.pi / 4.0),
            'left_front_leg': (legSpreadZ * 0.74, math.pi / 4.0),
        }
        for leg, (bz, by) in base_poses.items():
            if leg in part_nodes:
                part_nodes[leg]['rz'] = bz
                part_nodes[leg]['ry'] = by

        swing1 = -(math.cos(t * 2.0) * 0.35)
        swing2 = -(math.cos(t * 2.0 + math.pi) * 0.35)
        lift1 = max(0.0, math.sin(t * 2.0)) * 0.3
        lift2 = max(0.0, math.sin(t * 2.0 + math.pi)) * 0.3

        if 'right_hind_leg' in part_nodes:
            part_nodes['right_hind_leg']['ry'] += swing1
            part_nodes['right_hind_leg']['rz'] += lift1
        if 'left_hind_leg' in part_nodes:
            part_nodes['left_hind_leg']['ry'] -= swing1
            part_nodes['left_hind_leg']['rz'] -= lift2
        if 'right_middle_hind_leg' in part_nodes:
            part_nodes['right_middle_hind_leg']['ry'] += swing2
            part_nodes['right_middle_hind_leg']['rz'] += lift2
        if 'left_middle_hind_leg' in part_nodes:
            part_nodes['left_middle_hind_leg']['ry'] -= swing2
            part_nodes['left_middle_hind_leg']['rz'] -= lift1
        if 'right_middle_front_leg' in part_nodes:
            part_nodes['right_middle_front_leg']['ry'] += swing1
            part_nodes['right_middle_front_leg']['rz'] += lift1
        if 'left_middle_front_leg' in part_nodes:
            part_nodes['left_middle_front_leg']['ry'] -= swing1
            part_nodes['left_middle_front_leg']['rz'] -= lift2
        if 'right_front_leg' in part_nodes:
            part_nodes['right_front_leg']['ry'] += swing2
            part_nodes['right_front_leg']['rz'] += lift2
        if 'left_front_leg' in part_nodes:
            part_nodes['left_front_leg']['ry'] -= swing2
            part_nodes['left_front_leg']['rz'] -= lift1
        if 'head' in part_nodes:
            part_nodes['head']['ry'] = math.sin(t) * 0.2

    elif entity_id == 'glinthawk':
        flap = math.sin(t) * 0.45
        if 'right_wing' in part_nodes:
            part_nodes['right_wing']['rz'] = flap
            part_nodes['right_wing']['rx'] = math.cos(t) * 0.1
        if 'left_wing' in part_nodes:
            part_nodes['left_wing']['rz'] = -flap
            part_nodes['left_wing']['rx'] = math.cos(t) * 0.1
        if 'tail' in part_nodes:
            part_nodes['tail']['rx'] = -0.1 + math.cos(t * 0.5) * 0.08
        if 'body' in part_nodes:
            part_nodes['body']['oy'] += math.sin(t) * 1.5
        if 'head' in part_nodes:
            part_nodes['head']['rx'] = -math.sin(t) * 0.08

    elif entity_id == 'waterwing':
        flap = math.sin(t) * 0.42
        if 'right_wing' in part_nodes:
            part_nodes['right_wing']['rz'] = flap
            part_nodes['right_wing']['rx'] = math.cos(t) * 0.08
        if 'left_wing' in part_nodes:
            part_nodes['left_wing']['rz'] = -flap
            part_nodes['left_wing']['rx'] = math.cos(t) * 0.08
        if 'tail' in part_nodes:
            part_nodes['tail']['rx'] = -0.1 + math.cos(t * 0.5) * 0.06
        if 'body' in part_nodes:
            part_nodes['body']['oy'] += math.sin(t) * 1.5

    elif entity_id == 'autonomous_sonic_turret':
        if 'head' in part_nodes:
            part_nodes['head']['ry'] = t
        if 'sensor_dome' in part_nodes:
            part_nodes['sensor_dome']['ry'] = -t * 0.5
        if 'barrel_left' in part_nodes:
            part_nodes['barrel_left']['rx'] = math.sin(t * 2.0) * 0.05
        if 'barrel_right' in part_nodes:
            part_nodes['barrel_right']['rx'] = -math.sin(t * 2.0) * 0.05

    elif entity_id == 'warden_android':
        twitch = math.sin(t) * 0.2
        if 'left_antenna' in part_nodes: part_nodes['left_antenna']['rz'] = twitch
        if 'right_antenna' in part_nodes: part_nodes['right_antenna']['rz'] = -twitch
        if 'body' in part_nodes: part_nodes['body']['oy'] += math.sin(t) * 0.8
        if 'left_arm' in part_nodes: part_nodes['left_arm']['rx'] = math.cos(t) * 0.18
        if 'right_arm' in part_nodes: part_nodes['right_arm']['rx'] = -math.cos(t) * 0.18
        if 'head' in part_nodes:
            part_nodes['head']['rx'] = math.cos(t) * 0.08
            part_nodes['head']['ry'] = math.sin(t) * 0.12

    elif entity_id == 'sandworm':
        sway = math.sin(t) * 0.03
        sway_y = math.cos(t) * 0.02
        if 'base' in part_nodes:
            part_nodes['base']['rx'] = 0.15 + sway * 0.5
            part_nodes['base']['ry'] = 0.20 + sway_y
            part_nodes['base']['rz'] = 0.10
        for idx in range(1, 11):
            name = f'segment_{idx}'
            if name in part_nodes:
                undulate = math.sin(t - idx * 0.3) * 0.02
                if idx <= 4:
                    rx = 0.04 + undulate
                    ry = 0.03 + math.cos(t - idx * 0.3) * 0.015
                    rz = 0.02
                elif idx <= 7:
                    rx = -0.12 + undulate * 1.2
                    ry = -0.02 + math.cos(t - idx * 0.3) * 0.015
                    rz = -0.02
                else:
                    rx = -0.18 + undulate * 1.5
                    ry = -0.04 + math.cos(t - idx * 0.3) * 0.02
                    rz = -0.01
                part_nodes[name]['rx'] = rx
                part_nodes[name]['ry'] = ry
                part_nodes[name]['rz'] = rz
        if 'neck' in part_nodes:
            part_nodes['neck']['rx'] = -0.05 + math.sin(t - 3.2) * 0.02
            part_nodes['neck']['ry'] = 0.0 + math.cos(t - 3.2) * 0.02
            part_nodes['neck']['rz'] = 0.0
        if 'head' in part_nodes:
            part_nodes['head']['rx'] = -0.16 + math.sin(t - 3.5) * 0.02
            part_nodes['head']['ry'] = -0.68 + math.cos(t - 3.5) * 0.02
            part_nodes['head']['rz'] = 0.0
        if 'teeth_outer' in part_nodes: part_nodes['teeth_outer']['rz'] = t
        if 'teeth_middle' in part_nodes: part_nodes['teeth_middle']['rz'] = -t * 1.5
        if 'teeth_inner' in part_nodes: part_nodes['teeth_inner']['rz'] = t * 2.0

    elif entity_id == 'megazord':
        legSwing = math.cos(t) * 0.5
        if 'left_leg' in part_nodes: part_nodes['left_leg']['rx'] = legSwing
        if 'right_leg' in part_nodes: part_nodes['right_leg']['rx'] = -legSwing
        if 'left_arm' in part_nodes: part_nodes['left_arm']['rx'] = -legSwing * 0.7
        if 'right_arm' in part_nodes: part_nodes['right_arm']['rx'] = legSwing * 0.7
        if 'body' in part_nodes: part_nodes['body']['oy'] += abs(math.sin(t)) * 1.0

    elif entity_id == 'cyber_steed':
        limb = math.cos(t) * 0.65
        if 'rightFrontLeg' in part_nodes: part_nodes['rightFrontLeg']['rx'] = -limb
        if 'leftFrontLeg' in part_nodes: part_nodes['leftFrontLeg']['rx'] = limb
        if 'rightHindLeg' in part_nodes: part_nodes['rightHindLeg']['rx'] = limb
        if 'leftHindLeg' in part_nodes: part_nodes['leftHindLeg']['rx'] = -limb
        if 'tail' in part_nodes:
            part_nodes['tail']['rz'] = math.sin(t) * 0.15
            part_nodes['tail']['rx'] = 0.35 + math.sin(t * 2.0) * 0.06
        if 'head' in part_nodes:
            part_nodes['head']['rx'] = 0.1 + math.sin(t) * 0.08

    elif entity_id == 'cyber_hound':
        limb = math.cos(t) * 0.75
        if 'rightFrontLeg' in part_nodes: part_nodes['rightFrontLeg']['rx'] = -limb
        if 'leftFrontLeg' in part_nodes: part_nodes['leftFrontLeg']['rx'] = limb
        if 'rightHindLeg' in part_nodes: part_nodes['rightHindLeg']['rx'] = limb
        if 'leftHindLeg' in part_nodes: part_nodes['leftHindLeg']['rx'] = -limb
        if 'tail' in part_nodes:
            part_nodes['tail']['ry'] = math.sin(t * 2.0) * 0.4
        if 'head' in part_nodes:
            part_nodes['head']['rx'] = math.sin(t) * 0.1

def parse_model_hierarchy(file_path, entity_id=None, t=0.0):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    content_clean = re.sub(r'//.*', '', content)

    builder_vars = {}
    for m in re.finditer(r'CubeListBuilder\s+(\w+)\s*=\s*CubeListBuilder\.create\(\)(.*?);', content_clean, re.DOTALL):
        builder_vars[m.group(1)] = m.group(2)

    var_to_name = {'root': 'root'}
    for m in re.finditer(r'(?:PartDefinition\s+)?(\w+)\s*=\s*(\w+)\.addOrReplaceChild\s*\(\s*["\']([^"\']+)["\']', content_clean):
        var_to_name[m.group(1)] = m.group(3)

    is_sandworm = (entity_id == 'sandworm' or 'SandwormModel' in file_path)
    part_nodes = {}

    pattern = re.compile(
        r'(\w+)\.addOrReplaceChild\s*\(\s*["\']([^"\']+)["\']\s*,\s*(?:CubeListBuilder\.create\(\)|(\w+))(.*?)(PartPose\.ZERO|PartPose\.offsetAndRotation\s*\(.*?\)\s*\)|PartPose\.offset\s*\(.*?\))',
        re.DOTALL
    )

    for m in pattern.finditer(content_clean):
        parent_var = m.group(1)
        name = m.group(2)
        bvar = m.group(3)
        chunk = m.group(4)
        pose_str = m.group(5)

        if bvar and bvar in builder_vars:
            chunk = builder_vars[bvar] + chunk

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

        if is_sandworm and name.startswith('sub_ring'):
            continue

        boxes = []
        box_pattern = re.compile(
            r'texOffs\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)\s*(?:\.mirror\(\)\s*)?\.addBox\s*\(\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*,\s*(-?[\d\.]+)f?\s*\)'
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

    apply_entity_pose(part_nodes, entity_id, t)

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

def render_model_frames(frame_boxes_list, texture_path, output_png_path=None, output_gif_path=None, img_size=(400, 400), yaw_deg=-135, pitch_deg=30):
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

    all_global_pts = []
    for boxes in frame_boxes_list:
        for box in boxes:
            w_mat = box['matrix']
            x0, y0, z0 = box['x'], box['y'], box['z']
            dx, dy, dz = box['dx'], box['dy'], box['dz']
            for px in (x0, x0 + dx):
                for py in (y0, y0 + dy):
                    for pz in (z0, z0 + dz):
                        pw = w_mat @ np.array([px, py, pz, 1.0], dtype=np.float32)
                        pc = world_to_cam(pw[0], pw[1], pw[2])
                        all_global_pts.append(pc)

    if not all_global_pts:
        return

    xs = [p[0] for p in all_global_pts]
    ys = [p[1] for p in all_global_pts]
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

    frames_rgba = []

    for boxes in frame_boxes_list:
        canvas = np.zeros((target_h, target_w, 4), dtype=np.uint8)
        z_buffer = np.full((target_h, target_w), -1e9, dtype=np.float32)

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
                {'pts': [(x1, y0, z0), (x0, y0, z0), (x0, y0, z1), (x1, y0, z1)], 'uv': [(u2, v1), (u1, v1), (u1, v0), (u2, v0)], 'norm': (0, -1, 0)},
                {'pts': [(x1, y1, z1), (x0, y1, z1), (x0, y1, z0), (x1, y1, z0)], 'uv': [(u2, v0), (u3, v0), (u3, v1), (u2, v1)], 'norm': (0, 1, 0)},
                {'pts': [(x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0)], 'uv': [(u1, v1), (u2, v1), (u2, v2), (u1, v2)], 'norm': (0, 0, -1)},
                {'pts': [(x1, y0, z1), (x0, y0, z1), (x0, y1, z1), (x1, y1, z1)], 'uv': [(u3, v1), (u4, v1), (u4, v2), (u3, v2)], 'norm': (0, 0, 1)},
                {'pts': [(x0, y0, z1), (x0, y0, z0), (x0, y1, z0), (x0, y1, z1)], 'uv': [(u0, v1), (u1, v1), (u1, v2), (u0, v2)], 'norm': (-1, 0, 0)},
                {'pts': [(x1, y0, z0), (x1, y0, z1), (x1, y1, z1), (x1, y1, z0)], 'uv': [(u2, v1), (u3, v1), (u3, v2), (u2, v2)], 'norm': (1, 0, 0)}
            ]

            for face in faces:
                pts_world = []
                for p in face['pts']:
                    pw4 = w_mat @ np.array([p[0], p[1], p[2], 1.0], dtype=np.float32)
                    pts_world.append((pw4[0], pw4[1], pw4[2]))

                nw4 = w_mat @ np.array([face['norm'][0], face['norm'][1], face['norm'][2], 0.0], dtype=np.float32)
                nx, ny, nz = nw4[0], nw4[1], nw4[2]
                n_len = math.sqrt(nx*nx + ny*ny + nz*nz)
                if n_len > 1e-5:
                    nx, ny, nz = nx/n_len, ny/n_len, nz/n_len

                if ny < -0.5: light = 1.0
                elif ny > 0.5: light = 0.52
                elif abs(nz) > abs(nx): light = 0.82
                else: light = 0.68

                pts_cam = [world_to_cam(*pw) for pw in pts_world]

                tris = [
                    ([pts_cam[0], pts_cam[1], pts_cam[2]], [face['uv'][0], face['uv'][1], face['uv'][2]]),
                    ([pts_cam[0], pts_cam[2], pts_cam[3]], [face['uv'][0], face['uv'][2], face['uv'][3]])
                ]

                for tri_pts, tri_uvs in tris:
                    p0, p1, p2 = tri_pts
                    u0, u1, u2 = tri_uvs

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

                    px_vals = np.arange(min_px, max_px + 1)
                    py_vals = np.arange(min_py, max_py + 1)
                    if len(px_vals) == 0 or len(py_vals) == 0:
                        continue

                    PX, PY = np.meshgrid(px_vals, py_vals)
                    w0 = ((sy1 - sy2) * (PX - sx2) + (sx2 - sx1) * (PY - sy2)) / denom
                    w1 = ((sy2 - sy0) * (PX - sx2) + (sx0 - sx2) * (PY - sy2)) / denom
                    w2 = 1.0 - w0 - w1

                    mask = (w0 >= -0.01) & (w1 >= -0.01) & (w2 >= -0.01)
                    if not np.any(mask):
                        continue

                    depth = w0 * z0 + w1 * z1 + w2 * z2
                    sub_z = z_buffer[min_py:max_py+1, min_px:max_px+1]
                    valid = mask & (depth > sub_z)
                    if not np.any(valid):
                        continue

                    tu = (w0 * u0[0] + w1 * u1[0] + w2 * u2[0])[valid]
                    tv = (w0 * u0[1] + w1 * u1[1] + w2 * u2[1])[valid]

                    tx = (np.floor(tu).astype(np.int32)) % tex_w
                    ty = (np.floor(tv).astype(np.int32)) % tex_h

                    colors = tex_arr[ty, tx]
                    vis_mask = colors[:, 3] > 20
                    if not np.any(vis_mask):
                        continue

                    sub_z[valid] = np.where(vis_mask, depth[valid], sub_z[valid])
                    rgb = (colors[:, :3].astype(np.float32) * light).clip(0, 255).astype(np.uint8)
                    rgba = np.column_stack([rgb, colors[:, 3]])

                    sub_canvas = canvas[min_py:max_py+1, min_px:max_px+1]
                    sub_canvas[valid] = np.where(vis_mask[:, None], rgba, sub_canvas[valid])

        frames_rgba.append(Image.fromarray(canvas, mode='RGBA'))

    if output_png_path and frames_rgba:
        os.makedirs(os.path.dirname(os.path.abspath(output_png_path)), exist_ok=True)
        frames_rgba[0].save(output_png_path)
        print(f"Rendered PNG: {output_png_path}")

    if output_gif_path and len(frames_rgba) > 1:
        os.makedirs(os.path.dirname(os.path.abspath(output_gif_path)), exist_ok=True)
        gif_frames = []
        for im in frames_rgba:
            alpha = im.getchannel('A')
            mask = Image.eval(alpha, lambda a: 255 if a < 64 else 0)
            p_img = im.convert('RGB').quantize(colors=255)
            p_img.paste(255, mask)
            p_img.info['transparency'] = 255
            p_img.info['disposal'] = 2
            gif_frames.append(p_img)

        gif_frames[0].save(
            output_gif_path,
            save_all=True,
            append_images=gif_frames[1:],
            duration=60,
            loop=0,
            transparency=255,
            disposal=2
        )
        print(f"Rendered animated GIF ({len(frames_rgba)} frames): {output_gif_path}")

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
            rel_gif_path = f"docs/images/entities/{ent['id']}.gif"
            rel_png_path = f"docs/images/entities/{ent['id']}.png"
            if os.path.exists(rel_gif_path):
                img_src = rel_gif_path
            else:
                img_src = rel_png_path
            lines.append(f'    <td align="center" width="{col_pct}" valign="top">')
            lines.append(f'      <img src="{img_src}" width="140" alt="{ent["name"]}"><br>')
            lines.append(f'      <b>{ent["name"]}</b><br>')
            lines.append(f'      <sub>{ent["role"]}</sub>')
            lines.append('    </td>')
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
        anchor = "## 📖 Como Jogar (Wiki)"
        if anchor in content:
            new_content = content.replace(anchor, f"{gallery_block}\n\n---\n\n{anchor}")
        else:
            new_content = content + f"\n\n---\n\n{gallery_block}\n"

    with open(readme_path, "w", encoding="utf-8") as f:
        f.write(new_content)
    print("README.md updated successfully with dynamic entity gallery!")

def main():
    parser = argparse.ArgumentParser(description="Render entity gallery images/GIFs and update README.md")
    parser.add_argument("--force", action="store_true", help="Force re-rendering of all images and GIFs")
    parser.add_argument("--entities", type=str, default=None, help="Comma-separated list of entity IDs to render")
    args = parser.parse_args()

    filter_ids = set(args.entities.split(',')) if args.entities else None

    model_dir = "src/client/java/com/fhfelipefh/sandstorm/client/renderer"
    tex_dir = "src/main/resources/assets/sandstorm"
    out_dir = "docs/images/entities"
    os.makedirs(out_dir, exist_ok=True)

    print(f"Processing {len(ENTITIES)} entities...")
    for ent in ENTITIES:
        ent_id = ent['id']
        if filter_ids and ent_id not in filter_ids:
            continue

        is_animated = ent_id in ANIMATED_ENTITIES
        out_png = os.path.join(out_dir, f"{ent_id}.png")
        out_gif = os.path.join(out_dir, f"{ent_id}.gif") if is_animated else None

        png_exists = os.path.exists(out_png)
        gif_exists = (out_gif and os.path.exists(out_gif))

        if not args.force and png_exists and (not is_animated or gif_exists):
            print(f"Skipping {ent_id} (already rendered)")
            continue

        mf = os.path.join(model_dir, ent["model"])
        tf = os.path.join(tex_dir, ent["texture"])

        if not os.path.exists(mf):
            print(f"ERROR: Model file not found: {mf}")
            continue
        if not os.path.exists(tf):
            print(f"ERROR: Texture file not found: {tf}")
            continue

        num_frames = 16 if is_animated else 1
        frame_boxes_list = []
        for f in range(num_frames):
            t = 2.0 * math.pi * f / num_frames if is_animated else 0.0
            boxes = parse_model_hierarchy(mf, entity_id=ent_id, t=t)
            frame_boxes_list.append(boxes)

        render_model_frames(
            frame_boxes_list,
            tf,
            output_png_path=out_png,
            output_gif_path=out_gif,
            img_size=(400, 400)
        )

    update_readme()

if __name__ == "__main__":
    main()
