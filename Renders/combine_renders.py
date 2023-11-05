from os import listdir
from os.path import isfile, join
import cv2

def find_inbetween(str, c1, c2):
    start = str.find(c1)
    end = str.find(c2)
    return str[start + 1:end]

def find_max_render(path):
    files = [join(path, f) for f in listdir(path) if isfile(join(path, f))]
    file = max(files, key=lambda f : int(find_inbetween(f, "(", ")").split("x")[2]))
    return file

def combine_max_renders(paths):
    maxs = [find_max_render(p) for p in paths]
    ims = [cv2.imread(m, cv2.IMREAD_ANYCOLOR | cv2.IMREAD_ANYDEPTH) for m in maxs]
    weights = [m.split("x")[2].split(")")[0] for m in maxs]
    ims 
    

# Folder names of renders
Renders = ["Render1", "1699185455504"]

print(find_max_render(Renders[1]))
