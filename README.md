# PathTracer

This project is a backwards path tracer in order to estimate a solution to the rendering equation.
It uses importance sampling to select bounce rays that are closer to the normal of their collision,
as well as russian roullete to remove rays randomly that do not contribute as much to the final
image.
