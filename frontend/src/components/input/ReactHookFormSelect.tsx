import FormControl, { FormControlProps } from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import { Controller } from 'react-hook-form';

export type IProps = FormControlProps & {
  name: string;
  label: string;
  control: any;
  defaultValue: string;
  children: React.ReactNode;
};

const ReactHookFormSelect = ({ name, label, control, defaultValue, children, ...props }: IProps) => {
  const labelId = `${name}-label`;
  return (
    <FormControl {...props}>
      <InputLabel id={labelId}>{label}</InputLabel>
      <Controller
        as={
          <Select label={label} labelId={labelId}>
            {children}
          </Select>
        }
        control={control}
        defaultValue={defaultValue}
        name={name}
      />
    </FormControl>
  );
};
export default ReactHookFormSelect;
