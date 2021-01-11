
### Black Hole Classifier

This was written as part of [Code For Good 2017](http://codeforgood.io/) in Portland, Oregon. It was held July 27 through July 30th at Reed College.

## Setup

1) Download the data
2) Change the `DATA_ROOT` variable in `config.py`
3) Run `process_fits.py`
4) Run `crossvalidation.py`

The cross-validation is implemented as plug-and-play, so anyone can implement any classifier (e.g. a neural net in Tensorflow) and easily add it to the list of classifiers to be tested.

## WR Classifier Procedure

1. From the .fits file, get the wavelength values, flux values, and redshift.
    
```python
wavelength_values = 10**hdulist[1].data['loglam']
flux_values = hdulist[1].data['flux']
z = hdulist[2].data['z']
```
2. Scale the wavelengths by `1/(1+z)`, correcting for redshift
3. Adjust the slope of the curve
	1. Find the endpoints of the line by averaging the values around 4517±50 Å and 4785±50 Å
	2. Construct a line through those points
	3. At each point along the line, subtract the line's value from the flux
4. Apply a Gaussian Kernel to smooth the data and remove noise
5. Crop the range of samples to 4686±150 Å
6. Standardize the domain of wavelengths by interpolating between samples, reducing the number of samples to 300

To classify He2, we perform the above operations, then take the sum of values around 4686±5 Å. If the sum is less that 0, we reject it as not containing He2.

## Todo

- Todo - He2 classifier
	- using charts, check that the data's around y=0
	- add in a small threshold value, exclude values below the threshold
	- try different widths
	- check how many samples we're actually using
- Todo - WR classifier
	- Try multiple flux values
	- Look into other data in the fits file that could help train the classifier. What's contained in the other rows?
	- Find other features we can extract from the data? We could fit the overall curve to a blackbody curve, or fit the curve to a gaussian curve, then use the parameters from those to help build the classifier.

https://github.com/codeforgoodconf/black_holes_backend/blob/master/he2_classifier.py
