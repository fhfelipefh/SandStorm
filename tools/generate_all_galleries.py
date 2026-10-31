import argparse
import subprocess
import sys

def main():
    parser = argparse.ArgumentParser(description="Generate all galleries (entities, blocks, weapons) for SandStorm")
    parser.add_argument("--force", action="store_true", help="Force re-rendering all images")
    args = parser.parse_args()

    flags = ["--force"] if args.force else []

    print(">>> 1. Generating Entity Gallery...")
    subprocess.run([sys.executable, "tools/generate_entity_gallery.py"] + flags, check=True)

    print(">>> 2. Generating Block Gallery...")
    subprocess.run([sys.executable, "tools/generate_block_gallery.py"] + flags, check=True)

    print(">>> 3. Generating Weapon Gallery...")
    subprocess.run([sys.executable, "tools/generate_weapon_gallery.py"] + flags, check=True)

    print("All galleries generated and README.md updated successfully!")

if __name__ == "__main__":
    main()

