from os import listdir, mkdir
from os.path import isfile, join, isdir, exists
from shutil import copyfile

def copy_highest_renders(max_samples_folder = "CompleteRenders"):
    render_folders = [f for f in listdir("./") if (isdir(f) and f != max_samples_folder)]
    files = [[join(folder, f) for f in listdir(folder) if isfile(join(folder, f))] for folder in render_folders if len(listdir(folder)) > 0]
    maxs = [max(files[i], key=lambda f : int(f.split("x")[2].split(")")[0])) for i in range(len(files))]
    if not(exists(max_samples_folder)):
        mkdir(max_samples_folder)
    for f in maxs:
        copyfile(f, join(max_samples_folder, f.split("/")[1]))

copy_highest_renders()