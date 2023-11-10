from os import listdir
from os.path import isfile, join
import numpy as np
import cv2

def find_max_render(path):
    files = [join(path, f) for f in listdir(path) if isfile(join(path, f))]
    file = max(files, key=lambda f : int(f.split("x")[2].split(")")[0]))
    return file

def combine_max_renders(paths):
    maxs = [find_max_render(p) for p in paths]
    renders = [p.split("/")[len(p.split("/")) - 1] for p in paths]
    ims = [cv2.imread(m, cv2.IMREAD_ANYCOLOR | cv2.IMREAD_ANYDEPTH) for m in maxs]
    weights = np.array([int(m.split("x")[2].split(")")[0]) for m in maxs])
    average = np.sum(ims * weights[:,np.newaxis,np.newaxis,np.newaxis], axis=0) / np.sum(weights)
    print(f"Total Samples: {np.sum(weights)}")
    cv2.imwrite("CompleteRenders/" + "+".join(renders) + "(1).png", average)

# Folder names of renders
Renders = ["Render1", "1699185455504"]

combine_max_renders(Renders)
